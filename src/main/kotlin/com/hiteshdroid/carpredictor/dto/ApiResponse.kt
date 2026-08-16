package com.hiteshdroid.carpredictor.dto

import java.time.LocalDateTime

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errors: List<String> = emptyList(),
    val timestamp: LocalDateTime = LocalDateTime.now()
)

