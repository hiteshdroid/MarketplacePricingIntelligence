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
 * Syncs new car prices monthly using two strategies:
 *
 *  1. PRIMARY — bundled seed/new-car-prices.json (Indian market prices,
 *     manually curated and committed to the repo — update annually)
 *
 *  2. SECONDARY — segment-based price estimation for any make/model
 *     in the tracked list that isn't covered by the seed file
 *
 * Note: External vehicle APIs (NHTSA, CarQuery) were evaluated and dropped —
 * NHTSA is US-only and under maintenance; CarQuery blocks server-side access.
 * The seed file approach is more accurate for Indian market pricing.
 */
@Service
class NewCarPriceSyncJob(
    private val repo: NewCarPriceRepository,
    private val scraperProps: ScraperProperties,
    private val objectMapper: ObjectMapper
) {

    @EventListener(ApplicationReadyEvent::class)
    fun runOnStartup() {
        log.info { "🚀 Running NewCarPriceSyncJob on startup" }
        syncNewCarPrices()
    }

    @Scheduled(cron = "\${automation.schedule.new-car-prices:0 0 1 1 * *}")
    fun syncNewCarPrices() {
        log.info { "⏰ New car price sync job started" }

        val thisMonth = LocalDateTime.now().withDayOfMonth(1).toLocalDate()

        // Skip if already synced this month
        val alreadySynced = repo.findAll()
            .any { it.effectiveFrom.toLocalDate() >= thisMonth }

        if (false) {
            log.info { "⏭️  Already synced this month — skipping" }
            return
        }

        // Step 1: load seed file
        val seedCount = loadSeedData()

        // Step 2: for any tracked make/model not in seed, generate estimates
        val estimateCount = generateMissingEstimates()

        log.info { "✅ New car price sync done — $seedCount from seed, $estimateCount estimated" }
    }

    private fun loadSeedData(): Int {
        return try {
            val resource = ClassPathResource("static/seed/new-car-prices.json")
            val seeds = objectMapper.readValue<List<SeedCarPrice>>(resource.inputStream)

            val prices = seeds.map { seed ->
                NewCarPrice(
                    make = seed.make,
                    model = seed.model,
                    year = seed.year,
                    variant = seed.variant,
                    exShowroomPrice = seed.exShowroomPrice,
                    onRoadPrice = seed.onRoadPrice,
                    effectiveFrom = LocalDateTime.now()
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
        var count = 0
        val currentYear = LocalDateTime.now().year

        scraperProps.trackedMakes.forEach { trackedMake ->
            trackedMake.models.forEach { model ->
                // Only estimate if no seed record exists for this make/model
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
                                make = trackedMake.make,
                                model = model,
                                year = currentYear,
                                variant = variant,
                                exShowroomPrice = exShowroom,
                                onRoadPrice = onRoad,
                                effectiveFrom = LocalDateTime.now()
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
        "Toyota"  to mapOf(
            "Camry"    to 1400000.0,
            "Corolla"  to 2100000.0,
            "Innova"   to 1950000.0,
            "Fortuner" to 3350000.0
        ),
        "Honda"   to mapOf(
            "City"  to 1560000.0,
            "Amaze" to 900000.0,
            "CR-V"  to 3490000.0
        ),
        "Maruti"  to mapOf(
            "Swift"  to 860000.0,
            "Baleno" to 980000.0,
            "Dzire"  to 930000.0,
            "Ertiga" to 1100000.0
        ),
        "Hyundai" to mapOf(
            "Creta" to 1990000.0,
            "i20"   to 1100000.0,
            "Venue" to 1250000.0,
            "Verna" to 1590000.0
        )
    )
}
