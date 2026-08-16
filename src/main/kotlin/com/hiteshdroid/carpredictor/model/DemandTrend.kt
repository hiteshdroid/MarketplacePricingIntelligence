package com.hiteshdroid.carpredictor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "demand_trends")
data class DemandTrend(
    @Id val id: String? = null,
    val make: String,
    val model: String,
    val year: Int,
    val month: Int,
    val demandIndex: Double,          // 0.0 - 100.0
    val searchVolume: Int,
    val avgDaysToSell: Int,
    val region: String,
    val recordedAt: LocalDateTime = LocalDateTime.now()
)

