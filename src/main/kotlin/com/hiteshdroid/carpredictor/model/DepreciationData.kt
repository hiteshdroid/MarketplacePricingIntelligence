package com.hiteshdroid.carpredictor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "depreciation_data")
data class DepreciationData(
    @Id val id: String? = null,
    val make: String,
    val model: String,
    val ageYears: Int,
    val mileageBand: String,          // e.g. "0-20000", "20001-50000"
    val depreciationPercent: Double,  // e.g. 25.5 means 25.5% depreciation
    val fuelType: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

