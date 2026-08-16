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
@RequestMapping("/api/v1/demand-trends")
@Tag(name = "Demand Trends", description = "Track market demand trends for cars")
class DemandTrendController(private val service: DemandTrendService) {

    @PostMapping
    @Operation(summary = "Add demand trend data")
    fun add(@Valid @RequestBody req: DemandTrendRequest): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Trend added", service.addTrend(req)))

    @GetMapping
    @Operation(summary = "Get all demand trends")
    fun getAll(): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Trends fetched", service.getAll()))

    @GetMapping("/{id}")
    @Operation(summary = "Get trend by ID")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Trend fetched", service.getById(id)))

    @GetMapping("/search")
    @Operation(summary = "Get trends by make and model")
    fun search(
        @RequestParam make: String,
        @RequestParam model: String
    ): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Trends fetched", service.getByMakeAndModel(make, model)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a demand trend")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        service.deleteById(id)
        return ResponseEntity.ok(ApiResponse(true, "Trend deleted"))
    }
}

