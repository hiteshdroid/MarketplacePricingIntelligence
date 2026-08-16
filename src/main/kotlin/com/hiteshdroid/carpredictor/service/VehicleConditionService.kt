package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import org.springframework.stereotype.Service

@Service
class VehicleConditionService(private val repo: VehicleConditionRepository) {

    fun addCondition(req: VehicleConditionRequest): VehicleCondition {
        val entity = VehicleCondition(
            vin = req.vin, make = req.make, model = req.model,
            year = req.year, mileage = req.mileage,
            overallGrade = req.overallGrade, bodyCondition = req.bodyCondition,
            engineCondition = req.engineCondition, interiorCondition = req.interiorCondition,
            tyreCondition = req.tyreCondition, accidentHistory = req.accidentHistory,
            serviceHistoryAvailable = req.serviceHistoryAvailable,
            numberOfOwners = req.numberOfOwners, conditionScore = req.conditionScore
        )
        return repo.save(entity)
    }

    fun getAll(): List<VehicleCondition> = repo.findAll()

    fun getByVin(vin: String): VehicleCondition =
        repo.findByVin(vin) ?: throw NoSuchElementException("Vehicle condition not found for VIN: $vin")

    fun getById(id: String): VehicleCondition =
        repo.findById(id).orElseThrow { NoSuchElementException("Condition record not found: $id") }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Record not found: $id")
        repo.deleteById(id)
    }
}

