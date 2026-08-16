package com.hiteshdroid.carpredictor.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.service.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

@WebMvcTest(TransactionController::class)
@Import(TransactionControllerTest.MockConfig::class)
@ActiveProfiles("test")
class TransactionControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var transactionService: TransactionService

    @TestConfiguration
    class MockConfig {
        @Bean fun transactionService(): TransactionService = mockk()
    }

    private val sampleResponse = TransactionResponse(
        id = "txn1", make = "Toyota", model = "Camry", year = 2020,
        mileage = 30000, salePrice = 850000.0, saleDate = LocalDateTime.now(),
        location = "Mumbai", condition = "Good", fuelType = "Petrol",
        transmission = "Automatic", color = null
    )

    @Test
    fun `POST transactions should return 201`() {
        every { transactionService.addTransaction(any()) } returns sampleResponse

        val req = TransactionRequest(
            make = "Toyota", model = "Camry", year = 2020, mileage = 30000,
            salePrice = 850000.0, location = "Mumbai", condition = "Good",
            fuelType = "Petrol", transmission = "Automatic"
        )

        mockMvc.perform(
            post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.make").value("Toyota"))
    }

    @Test
    fun `GET transactions should return 200`() {
        every { transactionService.getAll() } returns listOf(sampleResponse)

        mockMvc.perform(get("/api/v1/transactions"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].make").value("Toyota"))
    }

    @Test
    fun `GET transaction by ID should return 200`() {
        every { transactionService.getById("txn1") } returns sampleResponse

        mockMvc.perform(get("/api/v1/transactions/txn1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value("txn1"))
    }

    @Test
    fun `GET transaction by ID not found should return 404`() {
        every { transactionService.getById("bad-id") } throws NoSuchElementException("Not found")

        mockMvc.perform(get("/api/v1/transactions/bad-id"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `POST transaction with invalid body should return 400`() {
        // Sending a TransactionRequest with blank make triggers @NotBlank validation
        val invalidReq = TransactionRequest(
            make = "",                // violates @NotBlank
            model = "Camry",
            year = 2020,
            mileage = 30000,
            salePrice = 850000.0,
            location = "Mumbai",
            condition = "Good",
            fuelType = "Petrol",
            transmission = "Automatic"
        )

        mockMvc.perform(
            post("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidReq))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.errors").isArray)
    }

    @Test
    fun `DELETE transaction should return 200`() {
        every { transactionService.deleteById("txn1") } returns Unit

        mockMvc.perform(delete("/api/v1/transactions/txn1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))

        verify { transactionService.deleteById("txn1") }
    }
}

@WebMvcTest(PricePredictionController::class)
@Import(PricePredictionControllerTest.MockConfig::class)
@ActiveProfiles("test")
class PricePredictionControllerTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var pricePredictionService: PricePredictionService

    @TestConfiguration
    class MockConfig {
        @Bean fun pricePredictionService(): PricePredictionService = mockk()
    }

    @Test
    fun `POST predict should return price prediction`() {
        val predictionResponse = PricePredictionResponse(
            make = "Toyota", model = "Camry", year = 2020, mileage = 30000,
            predictedPrice = 800000.0, priceRangeLow = 720000.0, priceRangeHigh = 880000.0,
            confidenceScore = 85.0,
            factors = PredictionFactors(
                avgTransactionPrice = 850000.0, avgCompetitionPrice = 820000.0,
                conditionScore = 8.5, demandIndex = 75.0,
                newCarOnRoadPrice = 1500000.0, depreciationPercent = 30.0
            )
        )
        every { pricePredictionService.predict(any()) } returns predictionResponse

        val req = PricePredictionRequest(
            make = "Toyota", model = "Camry", year = 2020,
            mileage = 30000, condition = "Good", location = "Mumbai",
            fuelType = "Petrol", transmission = "Automatic"
        )

        mockMvc.perform(
            post("/api/v1/predict")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.predictedPrice").value(800000.0))
            .andExpect(jsonPath("$.data.confidenceScore").value(85.0))
    }
}