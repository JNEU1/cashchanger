package com.cashchanger.cashchanger

import com.cashchanger.cashchanger.api.Input
import com.cashchanger.cashchanger.api.ErrorOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Rest controller to provide some mappings for change currencies, display exchange rates etc...
 *
 * @author Jan Neumann
 */
@RestController
class CashChangeController {

    private val INVALID_DATE = "Date can't be in the future, or too much in the past... go to /info to see manual"
    private val INVALID_REQUEST_CANNOT_PARSE = "Error when parsing JSON... go to /info to see manual"

    @Autowired
    private lateinit var service: CashChangeService

    /**
     * Exchange currency for given input params...
     * @see Input
     */
    @GetMapping("/change")
    fun changeCurrency(@RequestBody input: Input): String {
        var amount = service.calcAmount(input)
        return "you have $amount of ${input.to}"
    }

    /**
     * Show exchange rates for provided date
     */
    @GetMapping("/rates")
    fun getExchangeRates(@RequestBody date: LocalDate): String {
        var rates = service.findRates(date)
        return "for selected date $date are these exchange rates: \n$rates"
    }

    /**
     * Provide some manual
     */
    @GetMapping("/info")
    fun getInfo(): String {
        return """
            Go to /change to exchange currency... example message:
                {
                    "from": "CZK",
                    "to": "USD",
                    "amount": 200,
                    "date": "2021-08-9"
                }

            Or go to /rates to see exchange rate for given date... example message:
                "2021-08-5"
                        
            Given dates are only between 4.1.2021 and 17.9.2021
            You can exchange currencies: CZK, EUR, GBP, JPY and USD""".trimMargin()
    }

    /**
     * Handle NPE thrown when no record for given date is available
     */
    @ExceptionHandler(NullPointerException::class)
    fun handleInvalidDate(): ErrorOutput {
        return ErrorOutput(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST,
            NullPointerException::class.simpleName,
            INVALID_DATE
        )
    }

    /**
     * Handle Parsing exception thrown on invalid request
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleBadRequest(): ErrorOutput {
        return ErrorOutput(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST,
            HttpMessageNotReadableException::class.simpleName,
            INVALID_REQUEST_CANNOT_PARSE
        )
    }
}