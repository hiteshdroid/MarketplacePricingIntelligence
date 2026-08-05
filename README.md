# 🚗 Used Car Price Predictor

A production-ready REST API system for predicting used car prices using 6 key factors:
**Past Transactions · Competition Analysis · Vehicle Condition · Demand Trends · New Car Prices · Depreciation**

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 1.9 |
| Framework | Spring Boot 3.2 |
| Build | Gradle (Kotlin DSL) |
| Database | MongoDB |
| API Docs | OpenAPI 3 / Swagger UI |
| Testing | JUnit 5 + MockK |
| Coverage | JaCoCo (70% threshold) |
| CI/CD | GitHub Actions |

---

## 🚀 Getting Started

### Prerequisites

- JDK 17+
- MongoDB running locally on port `27017`
- Git

### Clone & Run

```bash
git clone https://github.com/hiteshdroid/used-car-price-predictor.git
cd used-car-price-predictor
./gradlew bootRun
```

App starts at: `http://localhost:8080`

### With custom MongoDB URI

```bash
MONGODB_URI=mongodb://localhost:27017/carpredictordb ./gradlew bootRun
```

---

## 📚 API Documentation

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
OpenAPI JSON: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 🔌 API Endpoints

### 🚗 Transactions `/api/v1/transactions`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add a past sale transaction |
| GET | `/` | Get all transactions |
| GET | `/{id}` | Get transaction by ID |
| GET | `/search?make=&model=` | Search transactions |
| GET | `/average-price?make=&model=&year=` | Get average sale price |
| DELETE | `/{id}` | Delete a transaction |

### 🏁 Competition `/api/v1/competition`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add competitor listing |
| GET | `/` | Get all listings |
| GET | `/summary?make=&model=&year=` | Get price summary |
| DELETE | `/{id}` | Delete a listing |

### 🔧 Conditions `/api/v1/conditions`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add condition assessment |
| GET | `/` | Get all assessments |
| GET | `/{id}` | Get by ID |
| GET | `/vin/{vin}` | Get by VIN |
| DELETE | `/{id}` | Delete record |

### 📈 Demand Trends `/api/v1/demand-trends`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add demand trend |
| GET | `/` | Get all trends |
| GET | `/search?make=&model=` | Search by make/model |
| DELETE | `/{id}` | Delete trend |

### 🏷️ New Car Prices `/api/v1/new-car-prices`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add new car price |
| GET | `/` | Get all prices |
| GET | `/latest?make=&model=&year=` | Get latest on-road price |
| DELETE | `/{id}` | Delete record |

### 📉 Depreciation `/api/v1/depreciation`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Add depreciation data |
| GET | `/` | Get all records |
| GET | `/calculate?make=&model=&ageYears=&fuelType=` | Calculate depreciation |
| DELETE | `/{id}` | Delete record |

### 🔮 Prediction `/api/v1/predict`
| Method | Path | Description |
|--------|------|-------------|
| POST | `/` | Predict price for a vehicle |

---

## 🧪 Running Tests

```bash
# Run all tests
./gradlew test

# Run with coverage report
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html

# Verify coverage threshold (70%)
./gradlew jacocoTestCoverageVerification
```

---

## 📬 Postman Collection

Import the collection from `postman/Used-Car-Price-Predictor.postman_collection.json`

Set the `baseUrl` variable to `http://localhost:8080`

---

## ⚙️ GitHub Actions CI/CD

CI pipeline runs on every push and PR to `main` and `develop`:

1. ✅ Build the project
2. ✅ Run all tests
3. ✅ Generate JaCoCo coverage report
4. ✅ Enforce 70% coverage threshold
5. ✅ Upload test reports as artifacts

---

## 📁 Project Structure

```
src/
├── main/kotlin/com/hiteshdroid/carpredictor/
│   ├── CarPredictorApplication.kt
│   ├── config/          # OpenAPI configuration
│   ├── controller/      # REST controllers
│   ├── service/         # Business logic
│   ├── repository/      # MongoDB repositories
│   ├── model/           # MongoDB documents
│   ├── dto/             # Request/Response DTOs
│   └── exception/       # Global exception handler
└── test/kotlin/
    ├── controller/      # Controller slice tests
    └── service/         # Service unit tests
```

---

## 🔮 Price Prediction Algorithm

The prediction engine combines all 6 factors with weighted scoring:

| Factor | Weight |
|--------|--------|
| Past Transaction Average | 40% |
| Competition Listing Average | 30% |
| New Car Price × (1 - Depreciation%) | 20% |
| Condition Score Multiplier | ±15% |
| Demand Index Multiplier | ±10% |

Confidence score is computed based on how many data points are available.
