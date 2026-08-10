package com.hiteshdroid.carpredictor.automation

import com.fasterxml.jackson.databind.ObjectMapper
import com.hiteshdroid.carpredictor.model.DemandTrend
import com.hiteshdroid.carpredictor.repository.DemandTrendRepository
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration
import java.time.LocalDateTime
import kotlin.random.Random

private val log = KotlinLogging.logger {}

/**
 * Derives demand trends from HuggingFace FinBERT sentiment analysis.
 *
 * WHERE HUGGINGFACE IS CALLED:
 *   updateDemandTrends()
 *     └── fetchSentimentOrFallback(make, model)        [per tracked make/model]
 *           └── fetchHuggingFaceSentiment(make, model) [HTTP POST to HuggingFace]
 *                 └── parseSentimentToDemandIndex()    [maps score → 0–100 index]
 *               OR (if HuggingFace is down/slow)
 *                 └── syntheticDemandIndex()           [seasonality fallback]
 *
 * WHEN IT RUNS:
 *  - On application startup (ApplicationReadyEvent) — seeds DB on first boot
 *  - Every 6 hours via cron — keeps demand index fresh
 *  - Manual trigger: POST /api/v1/automation/sync/demand-trends
 *
 * HuggingFace model: ProsusAI/finbert (financial sentiment)
 * Input : "The Toyota Camry used car market is showing strong demand..."
 * Output: [{label: "positive", score: 0.87}, {label: "negative"...}, {label: "neutral"...}]
 * Mapped: positive score × 70 + neutral × 15 - negative × 20 → clamped to [20, 95]
 */
@Service
class DemandTrendJob(
    private val repo: DemandTrendRepository,
    private val hfProps: HuggingFaceProperties,
    private val scraperProps: ScraperProperties,
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) {
    // ── Triggered once when the application is fully started ─────────────────
    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        log.info { "🚀 App ready — running demand trend update on startup" }
        updateDemandTrends()
    }

    // ── Every 6 hours — keeps demand index fresh ──────────────────────────────
    @Scheduled(cron = "\${automation.schedule.demand-trends:0 0 */6 * * *}")
    fun updateDemandTrends() {
        log.info { "⏰ Demand trend job started (HuggingFace FinBERT sentiment)" }
        var totalSaved = 0
        val now = LocalDateTime.now()

        scraperProps.trackedMakes.forEach { trackedMake ->
            trackedMake.models.forEach { model ->
                try {
                    // ← HuggingFace API is called here for each make/model
                    val demandIndex   = fetchSentimentOrFallback(trackedMake.make, model)
                    val avgDaysToSell = estimateDaysToSell(demandIndex)

                    val trend = DemandTrend(
                        make          = trackedMake.make,
                        model         = model,
                        year          = now.year,
                        month         = now.monthValue,
                        demandIndex   = demandIndex,
                        searchVolume  = estimateSearchVolume(trackedMake.make, model, demandIndex),
                        avgDaysToSell = avgDaysToSell,
                        region        = "India",
                        recordedAt    = now
                    )

                    // Upsert — replace existing record for same make/model/year/month
                    val existing = repo.findByMakeAndModelAndYear(trackedMake.make, model, now.year)
                        .firstOrNull { it.month == now.monthValue }

                    if (existing != null) repo.save(trend.copy(id = existing.id))
                    else repo.save(trend)

                    totalSaved++
                    log.debug { "  ${trackedMake.make} $model → demand index: ${"%.1f".format(demandIndex)}" }
                } catch (ex: Exception) {
                    log.warn { "  Failed demand trend for ${trackedMake.make} $model: ${ex.message}" }
                }
            }
        }
        log.info { "✅ Demand trend job done — $totalSaved records upserted" }
    }

    // ── HuggingFace call with fallback ────────────────────────────────────────

    private fun fetchSentimentOrFallback(make: String, model: String): Double {
        return try {
            fetchHuggingFaceSentiment(make, model)   // ← live HuggingFace call
        } catch (ex: Exception) {
            log.debug { "HuggingFace unavailable for $make $model — using seasonal fallback: ${ex.message}" }
            syntheticDemandIndex(make, model)         // ← fallback if API fails
        }
    }

    /**
     * POST to HuggingFace Inference API.
     * Model : ProsusAI/finbert
     * No API key required for free public inference (key just raises rate limits).
     * Timeout: 15s — falls back to synthetic on timeout.
     */
    private fun fetchHuggingFaceSentiment(make: String, model: String): Double {
        val prompt      = "The $make $model used car market is showing strong demand with competitive pricing and high search volumes."
        val requestBody = objectMapper.writeValueAsString(mapOf("inputs" to prompt))

        val headersAction: (HttpHeaders) -> Unit = { headers ->
            headers.contentType = MediaType.APPLICATION_JSON
            if (hfProps.apiKey.isNotBlank()) headers.setBearerAuth(hfProps.apiKey)
        }

        log.debug { "  Calling HuggingFace FinBERT for $make $model..." }

        val response = webClient.post()
            .uri(hfProps.apiUrl)
            .headers(headersAction)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono<String>()
            .timeout(Duration.ofSeconds(15))
            .block() ?: return syntheticDemandIndex(make, model)

        return parseSentimentToDemandIndex(response)
    }

    /**
     * Maps FinBERT output to a 0–100 demand index.
     *
     * FinBERT response: [[{label:"positive", score:0.87}, {label:"negative",...}, {label:"neutral",...}]]
     * Formula: (positive × 70) + (neutral × 15) - (negative × 20)
     * Clamped to [20, 95] — floor of 20 prevents zero-demand edge cases.
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseSentimentToDemandIndex(response: String): Double {
        return try {
            val parsed     = objectMapper.readValue(response, List::class.java)
            val sentiments = when (val first = parsed.firstOrNull()) {
                is List<*>  -> first as List<Map<String, Any>>
                is Map<*, *> -> parsed as List<Map<String, Any>>
                else        -> return syntheticDemandIndex("", "")
            }

            var positiveScore = 0.0
            var neutralScore  = 0.0
            var negativeScore = 0.0

            sentiments.forEach { item ->
                val label = item["label"]?.toString()?.lowercase() ?: ""
                val score = (item["score"] as? Number)?.toDouble() ?: 0.0
                when (label) {
                    "positive" -> positiveScore = score
                    "neutral"  -> neutralScore  = score
                    "negative" -> negativeScore = score
                }
            }

            val rawIndex = (positiveScore * 70) + (neutralScore * 15) - (negativeScore * 20)
            rawIndex.coerceIn(20.0, 95.0)
        } catch (ex: Exception) {
            log.debug { "Could not parse HuggingFace response: ${ex.message}" }
            syntheticDemandIndex("", "")
        }
    }

    // ── Seasonal fallback ─────────────────────────────────────────────────────

    /**
     * Used when HuggingFace is unreachable.
     * Indian car market peaks Oct–Jan (festival season), dips May–Jul (monsoon/off-season).
     */
    private fun syntheticDemandIndex(make: String, model: String): Double {
        val seasonalBoost = when (LocalDateTime.now().monthValue) {
            10, 11 -> 15.0   // Navratri / Diwali peak
            12, 1  -> 10.0   // Year-end deals / New Year
            2, 3   ->  5.0   // Moderate
            4      ->  0.0   // Transition
            5, 6   -> -10.0  // Off-season
            7      ->  -5.0  // Monsoon
            else   ->  2.0
        }
        val baseIndex = basePopularityIndex[make]?.get(model) ?: 50.0
        val noise     = Random.nextDouble(-5.0, 5.0)
        return (baseIndex + seasonalBoost + noise).coerceIn(15.0, 95.0)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun estimateDaysToSell(demandIndex: Double): Int = when {
        demandIndex >= 75 -> Random.nextInt(5,  15)
        demandIndex >= 50 -> Random.nextInt(15, 30)
        demandIndex >= 30 -> Random.nextInt(30, 50)
        else              -> Random.nextInt(50, 90)
    }

    private fun estimateSearchVolume(make: String, model: String, demandIndex: Double): Int {
        val baseVolume = baseSearchVolumes[make]?.get(model) ?: 5000
        return (baseVolume * (0.5 + demandIndex / 100.0)).toInt()
    }

    private val basePopularityIndex = mapOf(
        "Toyota"  to mapOf("Camry" to 55.0, "Corolla" to 60.0, "Innova" to 75.0, "Fortuner" to 70.0),
        "Honda"   to mapOf("City"  to 65.0, "Amaze"   to 55.0, "CR-V"   to 45.0),
        "Maruti"  to mapOf("Swift" to 85.0, "Baleno"  to 80.0, "Dzire"  to 78.0, "Ertiga"   to 72.0),
        "Hyundai" to mapOf("Creta" to 82.0, "i20"     to 70.0, "Venue"  to 68.0, "Verna"    to 62.0)
    )

    private val baseSearchVolumes = mapOf(
        "Toyota"  to mapOf("Camry" to 8000,  "Corolla" to 6000,  "Innova"  to 15000, "Fortuner" to 12000),
        "Honda"   to mapOf("City"  to 12000, "Amaze"   to 8000,  "CR-V"    to 4000),
        "Maruti"  to mapOf("Swift" to 25000, "Baleno"  to 20000, "Dzire"   to 18000, "Ertiga"   to 14000),
        "Hyundai" to mapOf("Creta" to 22000, "i20"     to 15000, "Venue"   to 13000, "Verna"    to 11000)
    )
}
