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
@RequestMapping("/api/v1/conditions")
@Tag(name = "Vehicle Condition", description = "Manage vehicle condition assessments")
class VehicleConditionController(private val service: VehicleConditionService) {

    @PostMapping
    @Operation(summary = "Add vehicle condition assessment")
    fun add(@Valid @RequestBody req: VehicleConditionRequest): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Condition added", service.addCondition(req)))

    @GetMapping
    @Operation(summary = "Get all condition records")
    fun getAll(): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Records fetched", service.getAll()))

    @GetMapping("/{id}")
    @Operation(summary = "Get condition by ID")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Record fetched", service.getById(id)))

    @GetMapping("/vin/{vin}")
    @Operation(summary = "Get condition by VIN")
    fun getByVin(@PathVariable vin: String): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Record fetched", service.getByVin(vin)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a condition record")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        service.deleteById(id)
        return ResponseEntity.ok(ApiResponse(true, "Record deleted"))
    }
}

