package com.hiteshdroid.carpredictor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class CarPredictorApplication

fun main(args: Array<String>) {
    runApplication<CarPredictorApplication>(*args)
}
