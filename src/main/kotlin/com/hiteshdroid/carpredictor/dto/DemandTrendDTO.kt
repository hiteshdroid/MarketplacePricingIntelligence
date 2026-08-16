package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*

data class DemandTrendRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2026) val year: Int,
    @field:Min(1) @field:Max(12) val month: Int,
    @field:DecimalMin("0.0") @field:DecimalMax("100.0") val demandIndex: Double,
    @field:Min(0) val searchVolume: Int,
    @field:Min(0) val avgDaysToSell: Int,
    @field:NotBlank val region: String
)

