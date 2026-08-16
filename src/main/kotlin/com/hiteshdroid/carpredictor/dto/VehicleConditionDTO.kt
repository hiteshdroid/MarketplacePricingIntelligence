package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*

data class VehicleConditionRequest(
    @field:NotBlank val vin: String,
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2026) val year: Int,
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

