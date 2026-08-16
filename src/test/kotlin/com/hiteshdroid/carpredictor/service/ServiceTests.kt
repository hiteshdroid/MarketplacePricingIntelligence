package com.hiteshdroid.carpredictor.service

import com.hiteshdroid.carpredictor.dto.*
import com.hiteshdroid.carpredictor.model.*
import com.hiteshdroid.carpredictor.repository.*
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime
import java.util.Optional

@ExtendWith(MockKExtension::class)
class TransactionServiceTest {

    @MockK lateinit var repo: TransactionRepository
    @InjectMockKs lateinit var service: TransactionService

    private val sampleTransaction = Transaction(
        id = "txn1", make = "Toyota", model = "Camry", year = 2020,
        mileage = 30000, salePrice = 850000.0, location = "Mumbai",
        condition = "Good", fuelType = "Petrol", transmission = "Automatic",
        saleDate = LocalDateTime.now()
    )

    @Test
    fun `addTransaction should save and return response`() {
        val req = TransactionRequest(
            make = "Toyota", model = "Camry", year = 2020, mileage = 30000,
            salePrice = 850000.0, location = "Mumbai", condition = "Good",
            fuelType = "Petrol", transmission = "Automatic"
        )
        every { repo.save(any()) } returns sampleTransaction

        val result = service.addTransaction(req)

        assertEquals("Toyota", result.make)
        assertEquals("Camry", result.model)
        assertEquals(850000.0, result.salePrice)
        verify { repo.save(any()) }
    }

    @Test
    fun `getAll should return all transactions`() {
        every { repo.findAll() } returns listOf(sampleTransaction)

        val result = service.getAll()

        assertEquals(1, result.size)
        assertEquals("Toyota", result[0].make)
    }

    @Test
    fun `getById should return transaction when found`() {
        every { repo.findById("txn1") } returns Optional.of(sampleTransaction)

        val result = service.getById("txn1")

        assertEquals("txn1", result.id)
    }

    @Test
    fun `getById should throw when not found`() {
        every { repo.findById("bad-id") } returns Optional.empty()

        assertThrows<NoSuchElementException> { service.getById("bad-id") }
    }

    @Test
    fun `getAverageSalePrice should return correct average`() {
        val txns = listOf(
            sampleTransaction.copy(salePrice = 800000.0),
            sampleTransaction.copy(salePrice = 900000.0)
        )
        every { repo.findByMakeAndModelAndYear("Toyota", "Camry", 2020) } returns txns

        val avg = service.getAverageSalePrice("Toyota", "Camry", 2020)

        assertEquals(850000.0, avg)
    }

    @Test
    fun `getAverageSalePrice should return 0 when no transactions`() {
        every { repo.findByMakeAndModelAndYear("X", "Y", 2020) } returns emptyList()

        val avg = service.getAverageSalePrice("X", "Y", 2020)

        assertEquals(0.0, avg)
    }

    @Test
    fun `deleteById should delete when exists`() {
        every { repo.existsById("txn1") } returns true
        every { repo.deleteById("txn1") } just Runs

        assertDoesNotThrow { service.deleteById("txn1") }
        verify { repo.deleteById("txn1") }
    }

    @Test
    fun `deleteById should throw when not found`() {
        every { repo.existsById("bad-id") } returns false

        assertThrows<NoSuchElementException> { service.deleteById("bad-id") }
    }
}

@ExtendWith(MockKExtension::class)
class CompetitionServiceTest {

    @MockK lateinit var repo: CompetitionListingRepository
    @InjectMockKs lateinit var service: CompetitionService

    private val sampleListing = CompetitionListing(
        id = "l1", make = "Honda", model = "City", year = 2021,
        mileage = 20000, listingPrice = 700000.0, source = "OLX",
        location = "Delhi", condition = "Good", daysOnMarket = 15
    )

    @Test
    fun `getSummary should compute correct stats`() {
        val listings = listOf(
            sampleListing.copy(listingPrice = 600000.0, daysOnMarket = 10),
            sampleListing.copy(listingPrice = 800000.0, daysOnMarket = 20)
        )
        every { repo.findByMakeAndModelAndYear("Honda", "City", 2021) } returns listings

        val summary = service.getSummary("Honda", "City", 2021)

        assertEquals(700000.0, summary.averageListingPrice)
        assertEquals(600000.0, summary.minPrice)
        assertEquals(800000.0, summary.maxPrice)
        assertEquals(2, summary.totalListings)
        assertEquals(15.0, summary.avgDaysOnMarket)
    }

    @Test
    fun `getSummary should return empty summary when no listings`() {
        every { repo.findByMakeAndModelAndYear("X", "Y", 2020) } returns emptyList()

        val summary = service.getSummary("X", "Y", 2020)

        assertEquals(0.0, summary.averageListingPrice)
        assertEquals(0, summary.totalListings)
    }
}

@ExtendWith(MockKExtension::class)
class DepreciationServiceTest {

    @MockK lateinit var repo: DepreciationDataRepository
    @InjectMockKs lateinit var service: DepreciationService

    @Test
    fun `getDepreciationPercent should return correct value from DB`() {
        val data = DepreciationData(
            make = "Maruti", model = "Swift", ageYears = 3,
            mileageBand = "20001-50000", depreciationPercent = 35.0, fuelType = "Petrol"
        )
        every { repo.findByMakeAndModelAndAgeYearsAndFuelType("Maruti", "Swift", 3, "Petrol") } returns listOf(data)

        val pct = service.getDepreciationPercent("Maruti", "Swift", 3, "Petrol")

        assertEquals(35.0, pct)
    }

    @Test
    fun `getDepreciationPercent should use default when no data`() {
        every { repo.findByMakeAndModelAndAgeYearsAndFuelType(any(), any(), any(), any()) } returns emptyList()
        every { repo.findByMakeAndModelAndAgeYears(any(), any(), any()) } returns emptyList()

        val pct = service.getDepreciationPercent("X", "Y", 6, "Diesel")

        assertEquals(60.0, pct) // default for 6 years
    }
}

@ExtendWith(MockKExtension::class)
class PricePredictionServiceTest {

    @MockK lateinit var transactionService: TransactionService
    @MockK lateinit var competitionService: CompetitionService
    @MockK lateinit var conditionRepo: VehicleConditionRepository
    @MockK lateinit var demandTrendService: DemandTrendService
    @MockK lateinit var newCarPriceService: NewCarPriceService
    @MockK lateinit var depreciationService: DepreciationService
    @InjectMockKs lateinit var service: PricePredictionService

    @Test
    fun `predict should return valid prediction response`() {
        val req = PricePredictionRequest(
            make = "Toyota", model = "Camry", year = 2020,
            mileage = 30000, condition = "Good", location = "Mumbai",
            fuelType = "Petrol", transmission = "Automatic"
        )

        every { transactionService.getAverageSalePrice("Toyota", "Camry", 2020) } returns 850000.0
        every { competitionService.getSummary("Toyota", "Camry", 2020) } returns
                CompetitionSummary("Toyota", "Camry", 2020, 820000.0, 750000.0, 900000.0, 5, 12.0)
        every { demandTrendService.getLatestDemandIndex("Toyota", "Camry") } returns 75.0
        every { newCarPriceService.getLatestOnRoadPrice("Toyota", "Camry", 2020) } returns 1500000.0
        every { depreciationService.getDepreciationPercent("Toyota", "Camry", any(), "Petrol") } returns 30.0

        val result = service.predict(req)

        assertNotNull(result)
        assertTrue(result.predictedPrice > 0)
        assertTrue(result.priceRangeLow < result.predictedPrice)
        assertTrue(result.priceRangeHigh > result.predictedPrice)
        assertTrue(result.confidenceScore in 0.0..100.0)
    }
}
