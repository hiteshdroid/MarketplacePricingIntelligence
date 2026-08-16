package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import org.springframework.stereotype.Service

@Service
class DepreciationService(private val repo: DepreciationDataRepository) {

    fun addDepreciation(req: DepreciationRequest): DepreciationData {
        val entity = DepreciationData(
            make = req.make, model = req.model, ageYears = req.ageYears,
            mileageBand = req.mileageBand, depreciationPercent = req.depreciationPercent,
            fuelType = req.fuelType
        )
        return repo.save(entity)
    }

    fun getAll(): List<DepreciationData> = repo.findAll()

    fun getById(id: String): DepreciationData =
        repo.findById(id).orElseThrow { NoSuchElementException("Depreciation data not found: $id") }

    fun getDepreciationPercent(make: String, model: String, ageYears: Int, fuelType: String): Double {
        val data = repo.findByMakeAndModelAndAgeYearsAndFuelType(make, model, ageYears, fuelType)
        if (data.isEmpty()) {
            // fallback to generic depreciation
            val generic = repo.findByMakeAndModelAndAgeYears(make, model, ageYears)
            return generic.firstOrNull()?.depreciationPercent ?: defaultDepreciation(ageYears)
        }
        return data.first().depreciationPercent
    }

    private fun defaultDepreciation(ageYears: Int): Double = when {
        ageYears <= 1 -> 15.0
        ageYears <= 3 -> 30.0
        ageYears <= 5 -> 45.0
        ageYears <= 8 -> 60.0
        else -> 70.0
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

