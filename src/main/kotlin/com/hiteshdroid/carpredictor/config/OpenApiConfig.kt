package com.hiteshdroid.carpredictor.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Used Car Price Predictor API")
                    .version("1.0.0")
                    .description(
                        """
                        REST API for predicting used car prices based on:
                        - Past transaction history
                        - Competition analysis
                        - Vehicle condition
                        - Demand trends
                        - New car price trends
                        - Depreciation factors
                        """.trimIndent()
                    )
                    .contact(
                        Contact()
                            .name("Hitesh")
                            .url("https://github.com/hiteshdroid/used-car-price-predictor")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
    }
}
