package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val log = KotlinLogging.logger {}

@Service
class TransactionService(private val repo: TransactionRepository) {

    fun addTransaction(req: TransactionRequest): TransactionResponse {
        log.debug { "Adding transaction for ${req.make} ${req.model}" }
        val entity = Transaction(
            make = req.make, model = req.model, year = req.year,
            mileage = req.mileage, salePrice = req.salePrice,
            location = req.location, condition = req.condition,
            fuelType = req.fuelType, transmission = req.transmission,
            color = req.color
        )
        return repo.save(entity).toResponse()
    }

    fun getAll(): List<TransactionResponse> = repo.findAll().map { it.toResponse() }

    fun getById(id: String): TransactionResponse =
        repo.findById(id).orElseThrow { NoSuchElementException("Transaction not found: $id") }.toResponse()

    fun getByMakeAndModel(make: String, model: String): List<TransactionResponse> =
        repo.findByMakeAndModel(make, model).map { it.toResponse() }

    fun getAverageSalePrice(make: String, model: String, year: Int): Double {
        val txns = repo.findByMakeAndModelAndYear(make, model, year)
        if (txns.isEmpty()) return 0.0
        return txns.map { it.salePrice }.average()
    }

    fun addTransactions(requests: List<TransactionRequest>): BulkTransactionResponse {
        log.debug { "Bulk adding ${requests.size} transactions" }
        val saved = mutableListOf<TransactionResponse>()
        val failed = mutableListOf<BulkTransactionError>()

        requests.forEachIndexed { index, req ->
            try {
                saved.add(addTransaction(req))
            } catch (ex: Exception) {
                log.warn { "  Failed item[$index] ${req.make} ${req.model}: ${ex.message}" }
                failed.add(BulkTransactionError(index = index, make = req.make, model = req.model, reason = ex.message ?: "Unknown error"))
            }
        }

        log.debug { "Bulk complete — saved: ${saved.size}, failed: ${failed.size}" }
        return BulkTransactionResponse(
            totalRequested = requests.size,
            totalSaved = saved.size,
            totalFailed = failed.size,
            saved = saved,
            failed = failed
        )
    }

    fun deleteById(id: String) {
        if (!repo.existsById(id)) throw NoSuchElementException("Transaction not found: $id")
        repo.deleteById(id)
    }

    private fun Transaction.toResponse() = TransactionResponse(
        id, make, model, year, mileage, salePrice, saleDate, location, condition, fuelType, transmission, color
    )
}

