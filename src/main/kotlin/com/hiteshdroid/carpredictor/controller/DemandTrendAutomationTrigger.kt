package com.hiteshdroid.carpredictor.controller

import com.hiteshdroid.carpredictor.automation.DemandTrendJob
import com.hiteshdroid.carpredictor.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/automation/sync/demand-trends")
@Tag(name = "Demand Trigger", description = "Trigger Demand")
class DemandTrendAutomationTrigger(private val service: DemandTrendJob) {

    @PostMapping
    @Operation(summary = "Predict used car price based on all available factors")
    fun trigger(): ResponseEntity<ApiResponse<Any>> {
        val result = service.updateDemandTrends()
        return ResponseEntity.ok(ApiResponse(true, "Price predicted successfully", result))
    }
}

