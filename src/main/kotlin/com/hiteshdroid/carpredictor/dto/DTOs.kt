package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

// ─── Transaction DTOs ────────────────────────────────────────────────────────

data class TransactionRequest(
    @field:NotBlank(message = "Make is required") val make: String,
    @field:NotBlank(message = "Model is required") val model: String,
    @field:Min(1980) @field:Max(2025) val year: Int,
    @field:Min(0) val mileage: Int,
    @field:Positive val salePrice: Double,
    @field:NotBlank val location: String,
    @field:NotBlank val condition: String,
    @field:NotBlank val fuelType: String,
    @field:NotBlank val transmission: String,
    val color: String? = null
)

data class TransactionResponse(
    val id: String?,
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val salePrice: Double,
    val saleDate: LocalDateTime,
    val location: String,
    val condition: String,
    val fuelType: String,
    val transmission: String,
    val color: String?
)

// ─── Competition DTOs ─────────────────────────────────────────────────────────

data class CompetitionListingRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2025) val year: Int,
    @field:Min(0) val mileage: Int,
    @field:Positive val listingPrice: Double,
    @field:NotBlank val source: String,
    @field:NotBlank val location: String,
    @field:NotBlank val condition: String,
    @field:Min(0) val daysOnMarket: Int
)

data class CompetitionSummary(
    val make: String,
    val model: String,
    val year: Int,
    val averageListingPrice: Double,
    val minPrice: Double,
    val maxPrice: Double,
    val totalListings: Int,
    val avgDaysOnMarket: Double
)

// ─── Vehicle Condition DTOs ───────────────────────────────────────────────────

data class VehicleConditionRequest(
    @field:NotBlank val vin: String,
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2025) val year: Int,
    @field:Min(0) val mileage: Int,
    @field:NotBlank val overallGrade: String,
    @field:NotBlank val bodyCondition: String,
    @field:NotBlank val engineCondition: String,
    @field:NotBlank val interiorCondition: String,
    @field:NotBlank val tyreCondition: String,
    val accidentHistory: Boolean,
    val serviceHistoryAvailable: Boolean,
    @field:Min(1) val numberOfOwners: Int,
    @field:DecimalMin("0.0") @field:DecimalMax("10.0") val conditionScore: Double
)

// ─── Demand Trend DTOs ────────────────────────────────────────────────────────

data class DemandTrendRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2025) val year: Int,
    @field:Min(1) @field:Max(12) val month: Int,
    @field:DecimalMin("0.0") @field:DecimalMax("100.0") val demandIndex: Double,
    @field:Min(0) val searchVolume: Int,
    @field:Min(0) val avgDaysToSell: Int,
    @field:NotBlank val region: String
)

// ─── New Car Price DTOs ───────────────────────────────────────────────────────

data class NewCarPriceRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2025) val year: Int,
    @field:NotBlank val variant: String,
    @field:Positive val exShowroomPrice: Double,
    @field:Positive val onRoadPrice: Double,
    val effectiveFrom: LocalDateTime,
    val effectiveTo: LocalDateTime? = null
)

// ─── Depreciation DTOs ────────────────────────────────────────────────────────

data class DepreciationRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(0) val ageYears: Int,
    @field:NotBlank val mileageBand: String,
    @field:DecimalMin("0.0") @field:DecimalMax("100.0") val depreciationPercent: Double,
    @field:NotBlank val fuelType: String
)

// ─── Price Prediction DTOs ────────────────────────────────────────────────────

data class PricePredictionRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2025) val year: Int,
    @field:Min(0) val mileage: Int,
    @field:NotBlank val condition: String,
    @field:NotBlank val location: String,
    @field:NotBlank val fuelType: String,
    @field:NotBlank val transmission: String,
    val vin: String? = null
)

data class PricePredictionResponse(
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val predictedPrice: Double,
    val priceRangeLow: Double,
    val priceRangeHigh: Double,
    val confidenceScore: Double,
    val factors: PredictionFactors
)

data class PredictionFactors(
    val avgTransactionPrice: Double?,
    val avgCompetitionPrice: Double?,
    val conditionScore: Double?,
    val demandIndex: Double?,
    val newCarOnRoadPrice: Double?,
    val depreciationPercent: Double?,
    val computedAt: LocalDateTime = LocalDateTime.now()
)

// ─── Generic API Response ─────────────────────────────────────────────────────

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errors: List<String> = emptyList(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)
