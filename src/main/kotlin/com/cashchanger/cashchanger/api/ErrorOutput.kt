package com.cashchanger.cashchanger.api

import org.springframework.http.HttpStatus
import java.time.LocalDateTime

data class ErrorOutput(
    val timestamp: LocalDateTime,
    val status: HttpStatus,
    val error: String?,
    val message: String
)