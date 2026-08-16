package com.hiteshdroid.carpredictor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "competition_listings")
data class CompetitionListing(
    @Id val id: String? = null,
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val listingPrice: Double,
    val source: String,
    val location: String,
    val condition: String,
    val daysOnMarket: Int,
    val listingDate: LocalDateTime = LocalDateTime.now(),
    val createdAt: LocalDateTime = LocalDateTime.now()
)

