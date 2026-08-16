package com.hiteshdroid.carpredictor.dto

import jakarta.validation.constraints.*
import java.time.LocalDateTime

data class TransactionRequest(
    @field:NotBlank(message = "Make is required") val make: String,
    @field:NotBlank(message = "Model is required") val model: String,
    @field:Min(1980) @field:Max(2026) val year: Int,
    @field:Min(0) val mileage: Int,
    @field:Positive val salePrice: Double,
    @field:NotBlank val location: String,
    @field:NotBlank val condition: String,
    @field:NotBlank val fuelType: String,
    @field:NotBlank val transmission: String,
    val color: String? = null
)

data class TransactionResponse(
    val id: String?,
    val make: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val salePrice: Double,
    val saleDate: LocalDateTime,
    val location: String,
    val condition: String,
    val fuelType: String,
    val transmission: String,
    val color: String?
)

data class BulkTransactionResponse(
    val totalRequested: Int,
    val totalSaved: Int,
    val totalFailed: Int,
    val saved: List<TransactionResponse>,
    val failed: List<BulkTransactionError>
)

data class BulkTransactionError(
    val index: Int,
    val make: String,
    val model: String,
    val reason: String
)

