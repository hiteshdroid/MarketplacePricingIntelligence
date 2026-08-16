package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import org.springframework.stereotype.Service

@Service
class CompetitionService(private val repo: CompetitionListingRepository) {

    fun addListing(req: CompetitionListingRequest): CompetitionListing {
        val entity = CompetitionListing(
            make = req.make, model = req.model, year = req.year,
            mileage = req.mileage, listingPrice = req.listingPrice,
            source = req.source, location = req.location,
            condition = req.condition, daysOnMarket = req.daysOnMarket
        )
        return repo.save(entity)
    }

    fun getAll(): List<CompetitionListing> = repo.findAll()

    fun getById(id: String): CompetitionListing =
        repo.findById(id).orElseThrow { NoSuchElementException("Listing not found: $id") }

    fun getSummary(make: String, model: String, year: Int): CompetitionSummary {
        val listings = repo.findByMakeAndModelAndYear(make, model, year)
        if (listings.isEmpty()) {
            return CompetitionSummary(make, model, year, 0.0, 0.0, 0.0, 0, 0.0)
        }
        val prices = listings.map { it.listingPrice }
        return CompetitionSummary(
            make = make, model = model, year = year,
            averageListingPrice = prices.average(),
            minPrice = prices.min(),
            maxPrice = prices.max(),
            totalListings = listings.size,
            avgDaysOnMarket = listings.map { it.daysOnMarket.toDouble() }.average()
        )
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Listing not found: $id")
        repo.deleteById(id)
    }
}

