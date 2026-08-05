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

@Repository
interface CompetitionListingRepository : MongoRepository<CompetitionListing, String> {
    fun findByMakeAndModel(make: String, model: String): List<CompetitionListing>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<CompetitionListing>
    fun findByLocation(location: String): List<CompetitionListing>
}

@Repository
interface VehicleConditionRepository : MongoRepository<VehicleCondition, String> {
    fun findByVin(vin: String): VehicleCondition?
    fun findByMakeAndModel(make: String, model: String): List<VehicleCondition>
}

@Repository
interface DemandTrendRepository : MongoRepository<DemandTrend, String> {
    fun findByMakeAndModel(make: String, model: String): List<DemandTrend>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<DemandTrend>
    fun findByMakeAndModelAndRegion(make: String, model: String, region: String): List<DemandTrend>
}

@Repository
interface NewCarPriceRepository : MongoRepository<NewCarPrice, String> {
    fun findByMakeAndModel(make: String, model: String): List<NewCarPrice>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<NewCarPrice>
}

@Repository
interface DepreciationDataRepository : MongoRepository<DepreciationData, String> {
    fun findByMakeAndModel(make: String, model: String): List<DepreciationData>
    fun findByMakeAndModelAndAgeYears(make: String, model: String, ageYears: Int): List<DepreciationData>
    fun findByMakeAndModelAndAgeYearsAndFuelType(
        make: String, model: String, ageYears: Int, fuelType: String
    ): List<DepreciationData>
}
