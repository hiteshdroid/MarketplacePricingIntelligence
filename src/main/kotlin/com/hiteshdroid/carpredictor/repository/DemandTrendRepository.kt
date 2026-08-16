package com.hiteshdroid.carpredictor.repository

import com.hiteshdroid.carpredictor.model.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface DemandTrendRepository : MongoRepository<DemandTrend, String> {
    fun findByMakeAndModel(make: String, model: String): List<DemandTrend>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<DemandTrend>
    fun findByMakeAndModelAndRegion(make: String, model: String, region: String): List<DemandTrend>
}

