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
@RequestMapping("/api/v1/competition")
@Tag(name = "Competition Analysis", description = "Track competitor car listings")
class CompetitionController(private val service: CompetitionService) {

    @PostMapping
    @Operation(summary = "Add a competition listing")
    fun add(@Valid @RequestBody req: CompetitionListingRequest): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Listing added", service.addListing(req)))

    @GetMapping
    @Operation(summary = "Get all competition listings")
    fun getAll(): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Listings fetched", service.getAll()))

    @GetMapping("/{id}")
    @Operation(summary = "Get listing by ID")
    fun getById(@PathVariable id: String): ResponseEntity<ApiResponse<Any>> =
        ResponseEntity.ok(ApiResponse(true, "Listing fetched", service.getById(id)))

    @GetMapping("/summary")
    @Operation(summary = "Get competition price summary for make/model/year")
    fun summary(
        @RequestParam make: String,
        @RequestParam model: String,
        @RequestParam year: Int
    ): ResponseEntity<ApiResponse<CompetitionSummary>> =
        ResponseEntity.ok(ApiResponse(true, "Summary fetched", service.getSummary(make, model, year)))

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a competition listing")
    fun delete(@PathVariable id: String): ResponseEntity<ApiResponse<Nothing>> {
        service.deleteById(id)
        return ResponseEntity.ok(ApiResponse(true, "Listing deleted"))
    }
}

