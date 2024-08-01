package com.cashchanger.cashchanger.dal

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface ExchangeRatesDao : CrudRepository<ExchangeRateEntity, Int> {
    fun findTopByDateBetweenOrderByDateDesc(dateStart: LocalDate, dateEnd: LocalDate): ExchangeRateEntity?
}