package com.hiteshdroid.carpredictor.repository

import com.hiteshdroid.carpredictor.model.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface TransactionRepository : MongoRepository<Transaction, String> {
    fun findByMakeAndModel(make: String, model: String): List<Transaction>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<Transaction>
    fun findByLocation(location: String): List<Transaction>

    @Query("{ 'make': ?0, 'model': ?1, 'year': ?2, 'mileage': { \$lte: ?3 } }")
    fun findSimilarTransactions(make: String, model: String, year: Int, mileage: Int): List<Transaction>
}

