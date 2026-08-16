package com.hiteshdroid.carpredictor.repository

import com.hiteshdroid.carpredictor.model.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DepreciationDataRepository : MongoRepository<DepreciationData, String> {
    fun findByMakeAndModel(make: String, model: String): List<DepreciationData>
    fun findByMakeAndModelAndAgeYears(make: String, model: String, ageYears: Int): List<DepreciationData>
    fun findByMakeAndModelAndAgeYearsAndFuelType(
        make: String, model: String, ageYears: Int, fuelType: String
    ): List<DepreciationData>
}

