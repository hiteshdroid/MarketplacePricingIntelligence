package com.hiteshdroid.carpredictor.controller

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/predict")
@Tag(name = "Price Prediction", description = "Predict used car prices using all factors")
class PricePredictionController(private val service: PricePredictionService) {

    @PostMapping
    @Operation(summary = "Predict used car price based on all available factors")
    fun predict(@Valid @RequestBody req: PricePredictionRequest): ResponseEntity<ApiResponse<PricePredictionResponse>> {
        val result = service.predict(req)
        return ResponseEntity.ok(ApiResponse(true, "Price predicted successfully", result))
    }
}

