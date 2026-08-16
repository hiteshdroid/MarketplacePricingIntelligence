package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*

data class DepreciationRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(0) val ageYears: Int,
    @field:NotBlank val mileageBand: String,
    @field:DecimalMin("0.0") @field:DecimalMax("100.0") val depreciationPercent: Double,
    @field:NotBlank val fuelType: String
)

