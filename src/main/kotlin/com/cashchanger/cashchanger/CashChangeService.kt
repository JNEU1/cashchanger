package com.cashchanger.cashchanger

import com.cashchanger.cashchanger.api.Currency.*
import com.cashchanger.cashchanger.api.Input
import com.cashchanger.cashchanger.dal.ExchangeRateEntity
import com.cashchanger.cashchanger.dal.ExchangeRatesDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Service class to calculate between currencies
 *
 * @author Jan Neumann
 */
@Component
class CashChangeService {

    // I guess on weekends/holidays some days are missing...
    private val MAX_PAST_DAYS_TO_SEARCH = 5L

    private val CZK_TO_OTHER = mapOf(
        CZK to { amount: Double, record: ExchangeRateEntity -> amount },
        EUR to { amount: Double, record: ExchangeRateEntity -> amount * record.czkToEur },
        GBP to { amount: Double, record: ExchangeRateEntity -> amount * record.czkToGbp },
        JPY to { amount: Double, record: ExchangeRateEntity -> amount * record.czkToJpy },
        USD to { amount: Double, record: ExchangeRateEntity -> amount * record.czkToUsd },
    )

    private val EUR_TO_OTHER = mapOf(
        CZK to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToEur },
        EUR to { amount: Double, record: ExchangeRateEntity -> amount },
        GBP to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToEur * record.czkToGbp },
        JPY to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToEur * record.czkToJpy },
        USD to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToEur * record.czkToUsd },
    )

    private val GBP_TO_OTHER = mapOf(
        CZK to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToGbp },
        EUR to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToGbp * record.czkToEur },
        GBP to { amount: Double, record: ExchangeRateEntity -> amount },
        JPY to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToGbp * record.czkToJpy },
        USD to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToGbp * record.czkToUsd },
    )

    private val JPY_TO_OTHER = mapOf(
        CZK to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToJpy },
        EUR to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToJpy * record.czkToEur },
        GBP to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToJpy * record.czkToGbp },
        JPY to { amount: Double, record: ExchangeRateEntity -> amount },
        USD to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToJpy * record.czkToUsd },
    )

    private val USD_TO_OTHER = mapOf(
        CZK to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToUsd },
        EUR to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToUsd * record.czkToEur },
        GBP to { amount: Double, record: ExchangeRateEntity -> (amount / record.czkToUsd) * record.czkToGbp },
        JPY to { amount: Double, record: ExchangeRateEntity -> amount / record.czkToUsd * record.czkToJpy },
        USD to { amount: Double, record: ExchangeRateEntity -> amount },
    )

    private val CURRENCY_TO_EXCHANGE_MAP = mapOf(
        CZK to CZK_TO_OTHER,
        EUR to EUR_TO_OTHER,
        GBP to GBP_TO_OTHER,
        JPY to JPY_TO_OTHER,
        USD to USD_TO_OTHER
    )

    @Autowired
    private lateinit var dao: ExchangeRatesDao;

    /**
     * On given input date find record with correct rates,
     * then find from matrix right function to return correct amount of toCurrency
     */
    fun calcAmount(input: Input): Double {
        var record =
            dao.findTopByDateBetweenOrderByDateDesc(input.date.minusDays(MAX_PAST_DAYS_TO_SEARCH), input.date)!!
        return CURRENCY_TO_EXCHANGE_MAP[input.from]!![input.to]
            ?.invoke(input.amount, record)!!
    }

    /**
     * return list of exchange rates for given day
     */
    fun findRates(date: LocalDate): String {
        var record = dao.findTopByDateBetweenOrderByDateDesc(date.minusDays(MAX_PAST_DAYS_TO_SEARCH), date)
        return """|     CZK to EUR: ${record!!.czkToEur}
        |     CZK to GBP: ${record.czkToGbp}
        |     CZK to JPY: ${record.czkToJpy}
        |     CZK to USD: ${record.czkToUsd}
        |     EUR to CZK: ${1 / record.czkToEur}
        |     EUR to GBP: ${record.czkToEur / record.czkToGbp}
        |     EUR to JPY: ${record.czkToEur / record.czkToJpy}
        |     EUR to USD: ${record.czkToEur / record.czkToUsd}
        |     GBP to CZK: ${1 / record.czkToGbp}
        |     GBP to EUR: ${record.czkToGbp / record.czkToEur}
        |     GBP to JPY: ${record.czkToGbp / record.czkToJpy}
        |     GBP to USD: ${record.czkToGbp / record.czkToUsd}
        |     JPY to CZK: ${1 / record.czkToJpy}
        |     JPY to EUR: ${record.czkToJpy / record.czkToEur}
        |     JPY to GPB: ${record.czkToJpy / record.czkToGbp}
        |     JPY to USD: ${record.czkToJpy / record.czkToUsd}
        |     USD to CZK: ${1 / record.czkToUsd}
        |     USD to EUR: ${record.czkToUsd / record.czkToEur}
        |     USD to GPB: ${record.czkToUsd / record.czkToGbp}
        |     USD to JPY: ${record.czkToUsd / record.czkToJpy}""".trimMargin()
    }
}