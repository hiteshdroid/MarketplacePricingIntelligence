package com.hiteshdroid.carpredictor.repository

import com.hiteshdroid.carpredictor.model.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface NewCarPriceRepository : MongoRepository<NewCarPrice, String> {
    fun findByMakeAndModel(make: String, model: String): List<NewCarPrice>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<NewCarPrice>
}

