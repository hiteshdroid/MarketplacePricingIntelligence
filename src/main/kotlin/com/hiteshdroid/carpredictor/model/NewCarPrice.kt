package com.hiteshdroid.carpredictor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "new_car_prices")
data class NewCarPrice(
    @Id val id: String? = null,
    val make: String,
    val model: String,
    val year: Int,
    val variant: String,
    val exShowroomPrice: Double,
    val onRoadPrice: Double,
    val effectiveFrom: LocalDateTime,
    val effectiveTo: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

