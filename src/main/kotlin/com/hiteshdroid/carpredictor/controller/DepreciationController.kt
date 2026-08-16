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
@RequestMapping("/api/v1/depreciation")
@Tag(name = "Depreciation", description = "Manage car depreciation data")
class DepreciationController(private val service: DepreciationService) {

    @PostMapping
    @Operation(summary = "Add depreciation data")
    fun add(@Valid @RequestBody req: DepreciationRequest): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Depreciation added", service.addDepreciation(req)))

    @GetMapping
    @Operation(summary = "Get all depreciation records")
    fun getAll(): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Records fetched", service.getAll()))

    @GetMapping("/{id}")
    @Operation(summary = "Get depreciation by ID")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Record fetched", service.getById(id)))

    @GetMapping("/calculate")
    @Operation(summary = "Get depreciation percentage for make/model/age/fuel")
    fun calculate(
        @RequestParam make: String,
        @RequestParam model: String,
        @RequestParam ageYears: Int,
        @RequestParam fuelType: String
    ): ResponseEntity<ApiResponse<Double>> =
        ResponseEntity.ok(ApiResponse(true, "Depreciation calculated",
            service.getDepreciationPercent(make, model, ageYears, fuelType)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a depreciation record")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        service.deleteById(id)
        return ResponseEntity.ok(ApiResponse(true, "Record deleted"))
    }
}

