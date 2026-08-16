package com.hiteshdroid.carpredictor.repository

import com.hiteshdroid.carpredictor.model.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface VehicleConditionRepository : MongoRepository<VehicleCondition, String> {
    fun findByVin(vin: String): VehicleCondition?
    fun findByMakeAndModel(make: String, model: String): List<VehicleCondition>
}

