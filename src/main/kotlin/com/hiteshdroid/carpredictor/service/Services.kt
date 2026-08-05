package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

// ─── Transaction Service ──────────────────────────────────────────────────────

@Service
class TransactionService(private val repo: TransactionRepository) {

    fun addTransaction(req: TransactionRequest): TransactionResponse {
        log.debug { "Adding transaction for ${req.make} ${req.model}" }
        val entity = Transaction(
            make = req.make, model = req.model, year = req.year,
            mileage = req.mileage, salePrice = req.salePrice,
            location = req.location, condition = req.condition,
            fuelType = req.fuelType, transmission = req.transmission,
            color = req.color
        )
        return repo.save(entity).toResponse()
    }

    fun getAll(): List<TransactionResponse> = repo.findAll().map { it.toResponse() }

    fun getById(id: String): TransactionResponse =
        repo.findById(id).orElseThrow { NoSuchElementException("Transaction not found: $id") }.toResponse()

    fun getByMakeAndModel(make: String, model: String): List<TransactionResponse> =
        repo.findByMakeAndModel(make, model).map { it.toResponse() }

    fun getAverageSalePrice(make: String, model: String, year: Int): Double {
        val txns = repo.findByMakeAndModelAndYear(make, model, year)
        if (txns.isEmpty()) return 0.0
        return txns.map { it.salePrice }.average()
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Transaction not found: $id")
        repo.deleteById(id)
    }

    private fun Transaction.toResponse() = TransactionResponse(
        id, make, model, year, mileage, salePrice, saleDate, location, condition, fuelType, transmission, color
    )
}

// ─── Competition Service ──────────────────────────────────────────────────────

@Service
class CompetitionService(private val repo: CompetitionListingRepository) {

    fun addListing(req: CompetitionListingRequest): CompetitionListing {
        val entity = CompetitionListing(
            make = req.make, model = req.model, year = req.year,
            mileage = req.mileage, listingPrice = req.listingPrice,
            source = req.source, location = req.location,
            condition = req.condition, daysOnMarket = req.daysOnMarket
        )
        return repo.save(entity)
    }

    fun getAll(): List<CompetitionListing> = repo.findAll()

    fun getById(id: String): CompetitionListing =
        repo.findById(id).orElseThrow { NoSuchElementException("Listing not found: $id") }

    fun getSummary(make: String, model: String, year: Int): CompetitionSummary {
        val listings = repo.findByMakeAndModelAndYear(make, model, year)
        if (listings.isEmpty()) {
            return CompetitionSummary(make, model, year, 0.0, 0.0, 0.0, 0, 0.0)
        }
        val prices = listings.map { it.listingPrice }
        return CompetitionSummary(
            make = make, model = model, year = year,
            averageListingPrice = prices.average(),
            minPrice = prices.min(),
            maxPrice = prices.max(),
            totalListings = listings.size,
            avgDaysOnMarket = listings.map { it.daysOnMarket.toDouble() }.average()
        )
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Listing not found: $id")
        repo.deleteById(id)
    }
}

// ─── Vehicle Condition Service ────────────────────────────────────────────────

@Service
class VehicleConditionService(private val repo: VehicleConditionRepository) {

    fun addCondition(req: VehicleConditionRequest): VehicleCondition {
        val entity = VehicleCondition(
            vin = req.vin, make = req.make, model = req.model,
            year = req.year, mileage = req.mileage,
            overallGrade = req.overallGrade, bodyCondition = req.bodyCondition,
            engineCondition = req.engineCondition, interiorCondition = req.interiorCondition,
            tyreCondition = req.tyreCondition, accidentHistory = req.accidentHistory,
            serviceHistoryAvailable = req.serviceHistoryAvailable,
            numberOfOwners = req.numberOfOwners, conditionScore = req.conditionScore
        )
        return repo.save(entity)
    }

    fun getAll(): List<VehicleCondition> = repo.findAll()

    fun getByVin(vin: String): VehicleCondition =
        repo.findByVin(vin) ?: throw NoSuchElementException("Vehicle condition not found for VIN: $vin")

    fun getById(id: String): VehicleCondition =
        repo.findById(id).orElseThrow { NoSuchElementException("Condition record not found: $id") }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

// ─── Demand Trend Service ─────────────────────────────────────────────────────

@Service
class DemandTrendService(private val repo: DemandTrendRepository) {

    fun addTrend(req: DemandTrendRequest): DemandTrend {
        val entity = DemandTrend(
            make = req.make, model = req.model, year = req.year,
            month = req.month, demandIndex = req.demandIndex,
            searchVolume = req.searchVolume, avgDaysToSell = req.avgDaysToSell,
            region = req.region
        )
        return repo.save(entity)
    }

    fun getAll(): List<DemandTrend> = repo.findAll()

    fun getById(id: String): DemandTrend =
        repo.findById(id).orElseThrow { NoSuchElementException("Demand trend not found: $id") }

    fun getByMakeAndModel(make: String, model: String): List<DemandTrend> =
        repo.findByMakeAndModel(make, model)

    fun getLatestDemandIndex(make: String, model: String): Double {
        val trends = repo.findByMakeAndModelAndYear(make, model, LocalDate.now().year)
        if (trends.isEmpty()) return 0.0
        return trends.maxByOrNull { it.month }?.demandIndex ?: 0.0
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

// ─── New Car Price Service ────────────────────────────────────────────────────

@Service
class NewCarPriceService(private val repo: NewCarPriceRepository) {

    fun addPrice(req: NewCarPriceRequest): NewCarPrice {
        val entity = NewCarPrice(
            make = req.make, model = req.model, year = req.year,
            variant = req.variant, exShowroomPrice = req.exShowroomPrice,
            onRoadPrice = req.onRoadPrice, effectiveFrom = req.effectiveFrom,
            effectiveTo = req.effectiveTo
        )
        return repo.save(entity)
    }

    fun getAll(): List<NewCarPrice> = repo.findAll()

    fun getById(id: String): NewCarPrice =
        repo.findById(id).orElseThrow { NoSuchElementException("New car price not found: $id") }

    fun getLatestOnRoadPrice(make: String, model: String, year: Int): Double {
        val prices = repo.findByMakeAndModelAndYear(make, model, year)
        if (prices.isEmpty()) return 0.0
        return prices.maxByOrNull { it.effectiveFrom }?.onRoadPrice ?: 0.0
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

// ─── Depreciation Service ─────────────────────────────────────────────────────

@Service
class DepreciationService(private val repo: DepreciationDataRepository) {

    fun addDepreciation(req: DepreciationRequest): DepreciationData {
        val entity = DepreciationData(
            make = req.make, model = req.model, ageYears = req.ageYears,
            mileageBand = req.mileageBand, depreciationPercent = req.depreciationPercent,
            fuelType = req.fuelType
        )
        return repo.save(entity)
    }

    fun getAll(): List<DepreciationData> = repo.findAll()

    fun getById(id: String): DepreciationData =
        repo.findById(id).orElseThrow { NoSuchElementException("Depreciation data not found: $id") }

    fun getDepreciationPercent(make: String, model: String, ageYears: Int, fuelType: String): Double {
        val data = repo.findByMakeAndModelAndAgeYearsAndFuelType(make, model, ageYears, fuelType)
        if (data.isEmpty()) {
            // fallback to generic depreciation
            val generic = repo.findByMakeAndModelAndAgeYears(make, model, ageYears)
            return generic.firstOrNull()?.depreciationPercent ?: defaultDepreciation(ageYears)
        }
        return data.first().depreciationPercent
    }

    private fun defaultDepreciation(ageYears: Int): Double = when {
        ageYears <= 1 -> 15.0
        ageYears <= 3 -> 30.0
        ageYears <= 5 -> 45.0
        ageYears <= 8 -> 60.0
        else -> 70.0
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

// ─── Price Prediction Service ─────────────────────────────────────────────────

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
