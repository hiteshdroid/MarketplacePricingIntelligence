package com.hiteshdroid.carpredictor.repository

import com.hiteshdroid.carpredictor.model.*
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CompetitionListingRepository : MongoRepository<CompetitionListing, String> {
    fun findByMakeAndModel(make: String, model: String): List<CompetitionListing>
    fun findByMakeAndModelAndYear(make: String, model: String, year: Int): List<CompetitionListing>
    fun findByLocation(location: String): List<CompetitionListing>
}

