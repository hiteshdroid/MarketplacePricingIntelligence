package com.hiteshdroid.carpredictor.automation

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@ConfigurationProperties(prefix = "automation.huggingface")
data class HuggingFaceProperties(
    val apiUrl: String = "https://api-inference.huggingface.co/models/ProsusAI/finbert",
    val apiKey: String = ""
)

@ConfigurationProperties(prefix = "automation.scraper")
data class ScraperProperties(
    val userAgent: String = "Mozilla/5.0 (compatible; CarPriceBot/1.0)",
    val timeoutMs: Int = 10000,
    val trackedMakes: List<TrackedMake> = emptyList()
)

data class TrackedMake(
    val make: String = "",
    val models: List<String> = emptyList()
)

@Configuration
@EnableConfigurationProperties(
    HuggingFaceProperties::class,
    ScraperProperties::class
)
class AutomationBeanConfig {

    @Bean
    fun webClient(): WebClient = WebClient.builder()
        .codecs { it.defaultCodecs().maxInMemorySize(2 * 1024 * 1024) }
        .build()
}
