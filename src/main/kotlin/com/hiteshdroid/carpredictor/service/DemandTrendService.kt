package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DemandTrendService(private val repo: DemandTrendRepository) {

    fun addTrend(req: DemandTrendRequest): DemandTrend {
        val entity = DemandTrend(
            make = req.make, model = req.model, year = req.year,
            month = req.month, demandIndex = req.demandIndex,
            searchVolume = req.searchVolume, avgDaysToSell = req.avgDaysToSell,
            region = req.region
        )
        return repo.save(entity)
    }

    fun getAll(): List<DemandTrend> = repo.findAll()

    fun getById(id: String): DemandTrend =
        repo.findById(id).orElseThrow { NoSuchElementException("Demand trend not found: $id") }

    fun getByMakeAndModel(make: String, model: String): List<DemandTrend> =
        repo.findByMakeAndModel(make, model)

    fun getLatestDemandIndex(make: String, model: String): Double {
        val trends = repo.findByMakeAndModelAndYear(make, model, LocalDate.now().year)
        if (trends.isEmpty()) return 0.0
        return trends.maxByOrNull { it.month }?.demandIndex ?: 0.0
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

