package com.hiteshdroid.carpredictor.automation

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hiteshdroid.carpredictor.model.NewCarPrice
import com.hiteshdroid.carpredictor.repository.NewCarPriceRepository
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

data class SeedCarPrice(
    val make: String,
    val model: String,
    val year: Int,
    val variant: String,
    val exShowroomPrice: Double,
    val onRoadPrice: Double
)

/**
 * Syncs new car prices using two strategies:
 *
 *  1. PRIMARY  — bundled seed/new-car-prices.json (Indian market prices,
 *                manually curated — update the JSON file annually)
 *  2. SECONDARY — segment-based estimates for any tracked make/model
 *                 not covered by the seed file
 *
 * WHEN IT RUNS:
 *  - On every application startup (ApplicationReadyEvent) — ensures DB
 *    is never empty when the server boots for the first time
 *  - Monthly cron (1st of month, 1 AM) — picks up annual price revisions
 *  - Manual trigger: POST /api/v1/automation/sync/new-car-prices
 *
 * Note: External vehicle APIs (NHTSA, CarQuery) were evaluated and dropped —
 * NHTSA is under maintenance; CarQuery blocks server-side access.
 * The seed file approach is more accurate for Indian market pricing.
 */
@Service
class NewCarPriceSyncJob(
    private val repo: NewCarPriceRepository,
    private val scraperProps: ScraperProperties,
    private val objectMapper: ObjectMapper
) {
    // ── Triggered once when the application is fully started ─────────────────
    @EventListener(ApplicationReadyEvent::class)
    fun onStartup() {
        log.info { "🚀 App ready — running new car price sync on startup" }
        syncNewCarPrices()
    }

    // ── Monthly cron — picks up price revisions ───────────────────────────────
    @Scheduled(cron = "\${automation.schedule.new-car-prices:0 0 1 1 * *}")
    fun syncNewCarPrices() {
        log.info { "⏰ New car price sync job started" }

        val thisMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate()

        // Skip if already synced this month (prevents double-run on same month)
        val alreadySynced = repo.findAll()
            .any { it.effectiveFrom.toLocalDate() >= thisMonth }

        if (alreadySynced) {
            log.info { "⏭️  Already synced this month — skipping" }
            return
        }

        val seedCount     = loadSeedData()
        val estimateCount = generateMissingEstimates()

        log.info { "✅ New car price sync done — $seedCount from seed, $estimateCount estimated" }
    }

    private fun loadSeedData(): Int {
        return try {
            val resource = ClassPathResource("static/seed/new-car-prices.json")
            val seeds    = objectMapper.readValue<List<SeedCarPrice>>(resource.inputStream)
            val prices   = seeds.map { seed ->
                NewCarPrice(
                    make             = seed.make,
                    model            = seed.model,
                    year             = seed.year,
                    variant          = seed.variant,
                    exShowroomPrice  = seed.exShowroomPrice,
                    onRoadPrice      = seed.onRoadPrice,
                    effectiveFrom    = LocalDateTime.now()
                )
            }
            repo.saveAll(prices)
            log.info { "  ✅ Loaded ${prices.size} records from seed file" }
            prices.size
        } catch (ex: Exception) {
            log.error { "  ❌ Failed to load seed file: ${ex.message}" }
            0
        }
    }

    private fun generateMissingEstimates(): Int {
        var count       = 0
        val currentYear = LocalDateTime.now().year

        scraperProps.trackedMakes.forEach { trackedMake ->
            trackedMake.models.forEach { model ->
                val existing = repo.findByMakeAndModelAndYear(trackedMake.make, model, currentYear)
                if (existing.isEmpty()) {
                    val base = segmentBasePrice(trackedMake.make, model)
                    listOf(
                        Triple("Base", base,         base * 1.10),
                        Triple("Mid",  base * 1.12,  base * 1.24),
                        Triple("Top",  base * 1.28,  base * 1.42)
                    ).forEach { (variant, exShowroom, onRoad) ->
                        repo.save(
                            NewCarPrice(
                                make            = trackedMake.make,
                                model           = model,
                                year            = currentYear,
                                variant         = variant,
                                exShowroomPrice = exShowroom,
                                onRoadPrice     = onRoad,
                                effectiveFrom   = LocalDateTime.now()
                            )
                        )
                        count++
                    }
                    log.debug { "  Generated estimates for ${trackedMake.make} $model" }
                }
            }
        }
        return count
    }

    private fun segmentBasePrice(make: String, model: String): Double =
        knownBasePrices[make]?.get(model) ?: 800000.0

    private val knownBasePrices = mapOf(
        "Toyota"  to mapOf("Camry" to 1400000.0, "Corolla" to 2100000.0, "Innova" to 1950000.0, "Fortuner" to 3350000.0),
        "Honda"   to mapOf("City"  to 1560000.0, "Amaze"   to 900000.0,  "CR-V"   to 3490000.0),
        "Maruti"  to mapOf("Swift" to 860000.0,  "Baleno"  to 980000.0,  "Dzire"  to 930000.0,  "Ertiga"  to 1100000.0),
        "Hyundai" to mapOf("Creta" to 1990000.0, "i20"     to 1100000.0, "Venue"  to 1250000.0, "Verna"   to 1590000.0)
    )
}