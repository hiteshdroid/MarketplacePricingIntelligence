package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class PricePredictionRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2026) val year: Int,
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

