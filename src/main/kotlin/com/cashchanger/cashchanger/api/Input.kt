package com.cashchanger.cashchanger.api

import java.time.LocalDate

/**
 * Input data for exchange currency operation
 */
data class Input(
    val from: Currency,
    val to: Currency,
    val amount: Double,
    val date: LocalDate
)