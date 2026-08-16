package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import org.springframework.stereotype.Service

@Service
class NewCarPriceService(private val repo: NewCarPriceRepository) {

    fun addPrice(req: NewCarPriceRequest): NewCarPrice {
        val entity = NewCarPrice(
            make = req.make, model = req.model, year = req.year,
            variant = req.variant, exShowroomPrice = req.exShowroomPrice,
            onRoadPrice = req.onRoadPrice, effectiveFrom = req.effectiveFrom,
            effectiveTo = req.effectiveTo
        )
        return repo.save(entity)
    }

    fun getAll(): List<NewCarPrice> = repo.findAll()

    fun getById(id: String): NewCarPrice =
        repo.findById(id).orElseThrow { NoSuchElementException("New car price not found: $id") }

    fun getLatestOnRoadPrice(make: String, model: String, year: Int): Double {
        val prices = repo.findByMakeAndModelAndYear(make, model, year)
        if (prices.isEmpty()) return 0.0
        return prices.maxByOrNull { it.effectiveFrom }?.onRoadPrice ?: 0.0
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

