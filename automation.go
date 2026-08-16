package main

import (
    "bytes"
    "context"
    "encoding/json"
    "io"
    "log"
    "math/rand"
    "net/http"
    "os"
    "time"

    "github.com/gin-gonic/gin"
    "go.mongodb.org/mongo-driver/bson"
)

type trackedMake struct { Make string; Models []string }
var trackedMakes = []trackedMake{{"Toyota",[]string{"Camry","Corolla","Innova","Fortuner"}},{"Honda",[]string{"City","Amaze","CR-V"}},{"Maruti",[]string{"Swift","Baleno","Dzire","Ertiga"}},{"Hyundai",[]string{"Creta","i20","Venue","Verna"}}}

func startAutomation(app *App) { go func(){ app.syncDemandTrends(context.Background()); app.syncNewCarPrices(context.Background()); demand:=time.NewTicker(6*time.Hour); prices:=time.NewTicker(24*time.Hour); defer demand.Stop();defer prices.Stop();for{select{case <-demand.C:app.syncDemandTrends(context.Background());case <-prices.C:app.syncNewCarPrices(context.Background())}}}() }
func (a *App) syncDemandTrends(ctx context.Context) int { saved:=0;now:=time.Now();for _,tm:=range trackedMakes{for _,model:=range tm.Models{idx:=fetchDemandIndex(ctx,tm.Make,model);trend:=DemandTrend{Make:tm.Make,Model:model,Year:now.Year(),Month:int(now.Month()),DemandIndex:idx,SearchVolume:estimateSearchVolume(tm.Make,model,idx),AvgDaysToSell:estimateDaysToSell(idx),Region:"India",RecordedAt:now};var existing DemandTrend;err:=a.db.Collection("demand_trends").FindOne(ctx,bson.M{"make":tm.Make,"model":model,"year":now.Year(),"month":int(now.Month())}).Decode(&existing);if err==nil{trend.ID=existing.ID;_,err=a.db.Collection("demand_trends").ReplaceOne(ctx,bson.M{"_id":existing.ID},trend)}else{_,err=a.db.Collection("demand_trends").InsertOne(ctx,trend)};if err==nil{saved++}}};log.Printf("demand trend sync: %d records",saved);return saved }
func fetchDemandIndex(ctx context.Context,make,model string)float64{endpoint:=getenv("HUGGINGFACE_API_URL","https://api-inference.huggingface.co/models/ProsusAI/finbert");payload,_:=json.Marshal(map[string]string{"inputs":"The "+make+" "+model+" used car market is showing strong demand with competitive pricing and high search volumes."});req,_:=http.NewRequestWithContext(ctx,http.MethodPost,endpoint,bytes.NewReader(payload));req.Header.Set("Content-Type","application/json");if key:=os.Getenv("HUGGINGFACE_API_KEY");key!=""{req.Header.Set("Authorization","Bearer "+key)};resp,err:=(http.Client{Timeout:15*time.Second}).Do(req);if err==nil{defer resp.Body.Close();body,_:=io.ReadAll(resp.Body);var groups [][]struct{Label string `json:"label"`;Score float64 `json:"score"`};if json.Unmarshal(body,&groups)==nil&&len(groups)>0{return sentimentIndex(groups[0])}};return syntheticDemandIndex(make,model)}
func sentimentIndex(items []struct{Label string `json:"label"`;Score float64 `json:"score"})float64{var p,n,neu float64;for _,x:=range items{switch x.Label{case "positive":p=x.Score;case "negative":n=x.Score;case "neutral":neu=x.Score}};v:=p*70+neu*15-n*20;if v<20{return 20};if v>95{return 95};return v}
func syntheticDemandIndex(make,model string)float64{base:=map[string]map[string]float64{"Toyota":{"Camry":55,"Corolla":60,"Innova":75,"Fortuner":70},"Honda":{"City":65,"Amaze":55,"CR-V":45},"Maruti":{"Swift":85,"Baleno":80,"Dzire":78,"Ertiga":72},"Hyundai":{"Creta":82,"i20":70,"Venue":68,"Verna":62}};v:=50.0;if m,ok:=base[make];ok{if x,ok:=m[model];ok{v=x}};switch time.Now().Month(){case 10,11:v+=15;case 12,1:v+=10;case 2,3:v+=5;case 5,6:v-=10;case 7:v-=5;default:v+=2};v+=rand.Float64()*10-5;if v<15{return 15};if v>95{return 95};return v}
func estimateDaysToSell(v float64)int{if v>=75{return 5+rand.Intn(10)};if v>=50{return 15+rand.Intn(15)};if v>=30{return 30+rand.Intn(20)};return 50+rand.Intn(40)}
func estimateSearchVolume(make,model string,d float64)int{base:=5000;if make=="Maruti"||make=="Hyundai"{base=15000};if make=="Toyota"{base=10000};return int(float64(base)*(0.5+d/100))}
type seedPrice struct{Make string `json:"make"`;Model string `json:"model"`;Year int `json:"year"`;Variant string `json:"variant"`;ExShowroomPrice float64 `json:"exShowroomPrice"`;OnRoadPrice float64 `json:"onRoadPrice"`}
func (a *App) syncNewCarPrices(ctx context.Context)int{path:=getenv("NEW_CAR_PRICE_SEED","src/main/resources/static/seed/new-car-prices.json");b,err:=os.ReadFile(path);if err!=nil{return 0};var seeds []seedPrice;if json.Unmarshal(b,&seeds)!=nil{return 0};saved:=0;for _,s:=range seeds{x:=NewCarPrice{Make:s.Make,Model:s.Model,Year:s.Year,Variant:s.Variant,ExShowroomPrice:s.ExShowroomPrice,OnRoadPrice:s.OnRoadPrice,EffectiveFrom:time.Now()};if _,err=a.db.Collection("new_car_prices").InsertOne(ctx,x);err==nil{saved++}};log.Printf("new car price sync: %d records",saved);return saved}
func (a *App) manualDemand(c *gin.Context){n:=a.syncDemandTrends(c);respond(c,200,"Demand trend sync completed",map[string]int{"saved":n})}
func (a *App) manualPrices(c *gin.Context){n:=a.syncNewCarPrices(c);respond(c,200,"New car price sync completed",map[string]int{"saved":n})}
