package main

import (
	"context"
	"log"
	"net/http"
	"os"
	"time"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type App struct {
	db *mongo.Database
}

func main() {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	uri := getenv("MONGODB_URI", "mongodb://localhost:27017")
	database := getenv("MONGODB_DATABASE", "carpredictordb")
	client, err := mongo.Connect(ctx, options.Client().ApplyURI(uri))
	if err != nil { log.Fatal(err) }
	if err = client.Ping(ctx, nil); err != nil { log.Fatal(err) }

	app := &App{db: client.Database(database)}
	r := gin.New()
	r.Use(gin.Logger(), gin.Recovery())
	r.GET("/health", func(c *gin.Context) { c.JSON(http.StatusOK, gin.H{"status": "UP"}) })
	registerRoutes(r, app)

	port := getenv("PORT", "8080")
	log.Printf("car price predictor listening on :%s", port)
	if err := r.Run(":" + port); err != nil { log.Fatal(err) }
}

func getenv(key, fallback string) string {
	if v := os.Getenv(key); v != "" { return v }
	return fallback
}
