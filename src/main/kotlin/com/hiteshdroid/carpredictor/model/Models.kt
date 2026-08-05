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
