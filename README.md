# Used Car Price Predictor — Go

Go implementation of the Used Car Price Predictor / Marketplace Pricing Intelligence API.

## Stack

- Go 1.24+
- Gin HTTP server
- MongoDB Go Driver
- GitHub Actions

## Run

```bash
go mod tidy
go run .
```

Environment variables:

- `PORT` — default `8080`
- `MONGODB_URI` — default `mongodb://localhost:27017`
- `MONGODB_DATABASE` — default `carpredictordb`

Health check: `GET /health`

## API

Base path: `/api/v1`

- `/transactions`
- `/competition`
- `/conditions`
- `/demand-trends`
- `/new-car-prices`
- `/depreciation`
- `/predict`

The Go implementation keeps the existing REST paths and MongoDB collection names so clients and existing data can be migrated without changing the public contract.

## Pricing engine

The prediction flow retains the existing weighting model:

- Historical transactions: 40%
- Competition listings: 30%
- Depreciated new-car price anchor: 20%
- Condition adjustment: ±15%
- Demand adjustment remains part of the model contract
- Confidence is based on transaction, competition and condition availability

## Migration

The Go migration lives on `go-migration`. The existing `main` branch remains unchanged.
