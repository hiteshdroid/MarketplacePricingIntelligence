package com.hiteshdroid.carpredictor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "transactions")
data class Transaction(
    @Id val id: String? = null,
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val salePrice: Double,
    val saleDate: LocalDateTime = LocalDateTime.now(),
    val location: String,
    val condition: String,
    val fuelType: String,
    val transmission: String,
    val color: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

