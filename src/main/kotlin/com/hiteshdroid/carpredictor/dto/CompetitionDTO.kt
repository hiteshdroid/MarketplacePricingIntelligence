package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*

data class CompetitionListingRequest(
    @field:NotBlank val make: String,
    @field:NotBlank val model: String,
    @field:Min(1980) @field:Max(2026) val year: Int,
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

