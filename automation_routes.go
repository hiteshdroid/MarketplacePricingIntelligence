package main

import "github.com/gin-gonic/gin"

func registerAutomationRoutes(r *gin.Engine, app *App) {
    v := r.Group("/api/v1/automation/sync")
    v.POST("/demand-trends", app.manualDemand)
    v.POST("/new-car-prices", app.manualPrices)
}
