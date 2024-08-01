package com.cashchanger.cashchanger

import com.cashchanger.cashchanger.dal.ExchangeRatesDao
import com.cashchanger.cashchanger.dal.ExchangeRateEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.streams.toList

/**
 * Load the csv file with historical currency exchange rates,
 * convert them to entity and save them to repository
 *
 * @see ExchangeRateEntity
 * @author Jan Neumann
 */
@Component
class InitExchangeRates {

    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    private val PATH: Path = Path.of("src/main/resources/exchangeRates.csv")

    @Autowired
    private lateinit var dao: ExchangeRatesDao;

    @EventListener(ApplicationReadyEvent::class)
    fun initExchangeRates() {
        try {
            Files.newBufferedReader(PATH, StandardCharsets.US_ASCII)
                .use { br ->
                    var toPersist = br.lines()
                        .map(this::convert)
                        .toList()
                    dao.saveAll(toPersist)
                }

        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }
    }

    private fun convert(fromCsv: String): ExchangeRateEntity {
        val splitted = fromCsv.split('|')
        return ExchangeRateEntity(
            LocalDate.parse(splitted[0], DATE_FORMATTER),
            splitted[1].replace(',', '.').toDouble(),
            splitted[2].replace(',', '.').toDouble(),
            splitted[3].replace(',', '.').toDouble(),
            splitted[4].replace(',', '.').toDouble()
        )
    }
}