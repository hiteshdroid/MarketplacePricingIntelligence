# 🚗 Used Car Price Predictor

A modular Spring Boot REST API for predicting used car prices. The codebase has been refactored into focused files: each controller, DTO, model, repository, and service lives in its own Kotlin file to improve maintainability and navigation.

What's included: Past Transactions, Competition Analysis, Vehicle Condition, Demand Trends, New Car Prices, Depreciation, and a Price Prediction endpoint that combines these factors.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Framework | Spring Boot |
| Build | Gradle (Kotlin DSL) |
| Database | MongoDB |
| API Docs | OpenAPI 3 / Swagger UI |
| Testing | JUnit 5 + MockK |
| Coverage | JaCoCo |

---

## 🚀 Getting Started

### Prerequisites

- JDK 17+
- MongoDB (default listens on port `27017`)
- Git

### Clone & Run

```bash
git clone https://github.com/hiteshdroid/used-car-price-predictor.git
cd used-car-price-predictor
./gradlew bootRun
```

The app starts at: `http://localhost:8080`

### Run with custom MongoDB URI

```bash
MONGODB_URI=mongodb://localhost:27017/carpredictordb ./gradlew bootRun
```

---

## 📚 API Documentation

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI JSON: http://localhost:8080/api-docs

---

## 🔌 API Endpoints (overview)

Base path: `/api/v1`

- Transactions: `/transactions`
- Competition: `/competition`
- Vehicle Conditions: `/conditions`
- Demand Trends: `/demand-trends`
- New Car Prices: `/new-car-prices`
- Depreciation: `/depreciation`
- Price Prediction: `/predict`
- Automation Trigger: `/automation/sync/demand-trends`

Refer to Swagger UI for full request/response schemas and examples.

---

## 📁 Code Layout (key files)

The repository structure has been modularized so each domain artifact is in its own file. Important folders and representative files:

```
src/main/kotlin/com/hiteshdroid/carpredictor/
├─ CarPredictorApplication.kt
├─ controller/
│  ├─ TransactionController.kt
│  ├─ CompetitionController.kt
│  ├─ VehicleConditionController.kt
│  ├─ DemandTrendController.kt
│  ├─ NewCarPriceController.kt
│  ├─ DepreciationController.kt
│  ├─ PricePredictionController.kt
│  └─ DemandTrendAutomationTrigger.kt
├─ dto/
│  ├─ TransactionDTO.kt
│  ├─ CompetitionDTO.kt
│  ├─ VehicleConditionDTO.kt
│  ├─ DemandTrendDTO.kt
│  ├─ NewCarPriceDTO.kt
│  ├─ DepreciationDTO.kt
│  ├─ PricePredictionDTO.kt
│  └─ ApiResponse.kt
├─ model/
│  ├─ Transaction.kt
│  ├─ CompetitionListing.kt
│  ├─ VehicleCondition.kt
│  ├─ DemandTrend.kt
│  ├─ NewCarPrice.kt
│  └─ DepreciationData.kt
├─ repository/
│  ├─ TransactionRepository.kt
│  ├─ CompetitionListingRepository.kt
│  ├─ VehicleConditionRepository.kt
│  ├─ DemandTrendRepository.kt
│  ├─ NewCarPriceRepository.kt
│  └─ DepreciationDataRepository.kt
└─ service/
   ├─ TransactionService.kt
   ├─ CompetitionService.kt
   ├─ VehicleConditionService.kt
   ├─ DemandTrendService.kt
   ├─ NewCarPriceService.kt
   ├─ DepreciationService.kt
   └─ PricePredictionService.kt
```

This modular layout replaces the previous monolithic `DTOs.kt`, `Models.kt`, `Repositories.kt`, and `Services.kt` files.

---

## 🔬 Price Prediction (brief)

The prediction engine aggregates multiple signals with the following intuition:

• Past transactions and competition listings provide historical and market prices.
• New car on-road price adjusted by depreciation gives an anchor.
• Vehicle condition and demand trends are used as multipliers/adjustments.

Weights and heuristics are implemented in `PricePredictionService.kt` — review that file for exact computation.

---

## 🧪 Running Tests

```bash
# Run tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport

# Open coverage report (macOS)
open build/reports/jacoco/test/html/index.html
```

---

## 📬 Postman

Import `postman/Used-Car-Price-Predictor.postman_collection.json` and set `baseUrl` to `http://localhost:8080`.

---

## ⚙️ CI/CD

The GitHub Actions workflow builds the project, runs tests, and publishes test artifacts. See `.github/workflows` for details.

---

If you'd like, I can also:

- Generate a concise developer README with commands and examples for common tasks (run, test, debug).
- Add a small CONTRIBUTING.md describing code style and commit conventions.

---
© Project maintained by the original repository authors.
