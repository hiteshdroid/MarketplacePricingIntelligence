package com.hiteshdroid.carpredictor.controller

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.service.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// ─── Transaction Controller ───────────────────────────────────────────────────

@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transactions", description = "Manage past car sale transactions")
class TransactionController(private val service: TransactionService) {

    @PostMapping
    @Operation(summary = "Add a new transaction")
    fun add(@Valid @RequestBody req: TransactionRequest): ResponseEntity<ApiResponse<TransactionResponse>> {
        val result = service.addTransaction(req)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "Transaction added successfully", result))
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

// ─── Competition Controller ───────────────────────────────────────────────────

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

// ─── Vehicle Condition Controller ─────────────────────────────────────────────

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

// ─── Demand Trend Controller ──────────────────────────────────────────────────

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

// ─── New Car Price Controller ─────────────────────────────────────────────────

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

// ─── Depreciation Controller ──────────────────────────────────────────────────

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

// ─── Price Prediction Controller ──────────────────────────────────────────────

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
