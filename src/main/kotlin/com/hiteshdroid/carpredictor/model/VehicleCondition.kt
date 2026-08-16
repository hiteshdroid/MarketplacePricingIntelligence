package com.hiteshdroid.carpredictor.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "vehicle_conditions")
data class VehicleCondition(
    @Id val id: String? = null,
    val vin: String,
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val overallGrade: String,        // Excellent, Good, Fair, Poor
    val bodyCondition: String,
    val engineCondition: String,
    val interiorCondition: String,
    val tyreCondition: String,
    val accidentHistory: Boolean,
    val serviceHistoryAvailable: Boolean,
    val numberOfOwners: Int,
    val conditionScore: Double,       // 0.0 - 10.0
    val assessedAt: LocalDateTime = LocalDateTime.now()
)

