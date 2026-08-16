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
@RequestMapping("/api/v1/new-car-prices")
@Tag(name = "New Car Prices", description = "Manage new car price references")
class NewCarPriceController(private val service: NewCarPriceService) {

    @PostMapping
    @Operation(summary = "Add new car price")
    fun add(@Valid @RequestBody req: NewCarPriceRequest): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Price added", service.addPrice(req)))

    @GetMapping
    @Operation(summary = "Get all new car prices")
    fun getAll(): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Prices fetched", service.getAll()))

    @GetMapping("/{id}")
    @Operation(summary = "Get price by ID")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Price fetched", service.getById(id)))

    @GetMapping("/latest")
    @Operation(summary = "Get latest on-road price for make/model/year")
    fun latest(
        @RequestParam make: String,
        @RequestParam model: String,
        @RequestParam year: Int
    ): ResponseEntity<ApiResponse<Double>> =
        ResponseEntity.ok(ApiResponse(true, "Latest price fetched", service.getLatestOnRoadPrice(make, model, year)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a new car price record")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        service.deleteById(id)
        return ResponseEntity.ok(ApiResponse(true, "Record deleted"))
    }
}

