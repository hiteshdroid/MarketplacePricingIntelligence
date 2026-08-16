package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class NewCarPriceRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2026) val year: Int,
    @field:NotBlank val variant: String,
    @field:Positive val exShowroomPrice: Double,
    @field:Positive val onRoadPrice: Double,
    val effectiveFrom: LocalDateTime,
    val effectiveTo: LocalDateTime? = null
)

