package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.repository.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate

private val log = KotlinLogging.logger {}

@Service
class PricePredictionService(
    private val transactionService: TransactionService,
    private val competitionService: CompetitionService,
    private val conditionRepo: VehicleConditionRepository,
    private val demandTrendService: DemandTrendService,
    private val newCarPriceService: NewCarPriceService,
    private val depreciationService: DepreciationService
) {
    fun predict(req: PricePredictionRequest): PricePredictionResponse {
        log.info { "Predicting price for ${req.make} ${req.model} ${req.year}" }

        val ageYears = LocalDate.now().year - req.year

        val avgTxnPrice = transactionService.getAverageSalePrice(req.make, req.model, req.year)
        val competitionSummary = competitionService.getSummary(req.make, req.model, req.year)
        val conditionScore = req.vin?.let {
            conditionRepo.findByVin(it)?.conditionScore
        }
        val demandIndex = demandTrendService.getLatestDemandIndex(req.make, req.model)
        val newCarPrice = newCarPriceService.getLatestOnRoadPrice(req.make, req.model, req.year)
        val depreciationPct = depreciationService.getDepreciationPercent(req.make, req.model, ageYears, req.fuelType)

        val predictedPrice = computePrice(
            avgTxnPrice = avgTxnPrice,
            avgCompetitionPrice = competitionSummary.averageListingPrice,
            conditionScore = conditionScore,
            demandIndex = demandIndex,
            newCarPrice = newCarPrice,
            depreciationPct = depreciationPct
        )

        val confidence = computeConfidence(avgTxnPrice, competitionSummary.totalListings, conditionScore)

        return PricePredictionResponse(
            make = req.make, model = req.model, year = req.year, mileage = req.mileage,
            predictedPrice = predictedPrice,
            priceRangeLow = predictedPrice * 0.90,
            priceRangeHigh = predictedPrice * 1.10,
            confidenceScore = confidence,
            factors = PredictionFactors(
                avgTransactionPrice = avgTxnPrice.takeIf { it > 0 },
                avgCompetitionPrice = competitionSummary.averageListingPrice.takeIf { it > 0 },
                conditionScore = conditionScore,
                demandIndex = demandIndex.takeIf { it > 0 },
                newCarOnRoadPrice = newCarPrice.takeIf { it > 0 },
                depreciationPercent = depreciationPct
            )
        )
    }

    private fun computePrice(
        avgTxnPrice: Double,
        avgCompetitionPrice: Double,
        conditionScore: Double?,
        demandIndex: Double,
        newCarPrice: Double,
        depreciationPct: Double
    ): Double {
        val sources = mutableListOf<Double>()

        if (avgTxnPrice > 0) sources.add(avgTxnPrice * 0.40)
        if (avgCompetitionPrice > 0) sources.add(avgCompetitionPrice * 0.30)
        if (newCarPrice > 0) sources.add(newCarPrice * (1 - depreciationPct / 100) * 0.20)

        var price = if (sources.isEmpty()) 0.0 else sources.sum()

        // Condition adjustment
        if (conditionScore != null) {
            val conditionMultiplier = 0.85 + (conditionScore / 10.0) * 0.30
            price *= conditionMultiplier
        }

        // Demand adjustment (±10%)
        if (demandIndex > 0) {
            val demandMultiplier = 0.90 + (demandIndex / 100.0) * 0.20
            price *= demandMultiplier
        }

        return price.coerceAtLeast(0.0)
    }

    private fun computeConfidence(avgTxnPrice: Double, competitionListings: Int, conditionScore: Double?): Double {
        var score = 0.0
        if (avgTxnPrice > 0) score += 0.40
        if (competitionListings > 0) score += 0.30
        if (conditionScore != null) score += 0.20
        score += 0.10 // base
        return (score * 100).coerceIn(0.0, 100.0)
    }
}

