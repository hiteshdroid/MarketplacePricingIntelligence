package com.hiteshdroid.carpredictor.controller

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Manage past car sale transactions")
class TransactionController(private val service: TransactionService) {

    @PostMapping
    @Operation(summary = "Add a single transaction")
    fun add(@Valid @RequestBody req: TransactionRequest): ResponseEntity<ApiResponse<TransactionResponse>> {
        val result = service.addTransaction(req)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Transaction added successfully", result))
    }

    @PostMapping("/bulk")
    @Operation(
        summary = "Add multiple transactions in one call",
        description = "Accepts an array of transactions. Each item is validated independently. " +
                "Failed items are reported in the response without blocking the rest."
    )
    fun addBulk(
        @RequestBody requests: List<@Valid TransactionRequest>
    ): ResponseEntity<ApiResponse<BulkTransactionResponse>> {
        if (requests.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(ApiResponse(false, "Request list cannot be empty"))
        }
        if (requests.size > 500) {
            return ResponseEntity.badRequest()
                .body(ApiResponse(false, "Maximum 500 transactions per bulk request"))
        }
        val result = service.addTransactions(requests)
        val status = if (result.totalFailed == 0) HttpStatus.CREATED else HttpStatus.MULTI_STATUS
        val message = "Bulk complete — ${result.totalSaved} saved, ${result.totalFailed} failed"
        return ResponseEntity.status(status)
            .body(ApiResponse(true, message, result))
    }

    @GetMapping
    @Operation(summary = "Get all transactions")
    fun getAll(): ResponseEntity<ApiResponse<List<TransactionResponse>>> =
        ResponseEntity.ok(ApiResponse(true, "Transactions fetched", service.getAll()))

    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<TransactionResponse>> =
        ResponseEntity.ok(ApiResponse(true, "Transaction fetched", service.getById(id)))

    @GetMapping("/search")
    @Operation(summary = "Search transactions by make and model")
    fun search(
        @RequestParam make: String,
        @RequestParam model: String
    ): ResponseEntity<ApiResponse<List<TransactionResponse>>> =
        ResponseEntity.ok(ApiResponse(true, "Transactions fetched", service.getByMakeAndModel(make, model)))

    @GetMapping("/average-price")
    @Operation(summary = "Get average sale price for a make/model/year")
    fun averagePrice(
        @RequestParam make: String,
        @RequestParam model: String,
        @RequestParam year: Int
    ): ResponseEntity<ApiResponse<Double>> =
        ResponseEntity.ok(ApiResponse(true, "Average price computed", service.getAverageSalePrice(make, model, year)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        service.deleteById(id)
        return ResponseEntity.ok(ApiResponse(true, "Transaction deleted"))
    }
}

