package com.cashchanger.cashchanger

import com.cashchanger.cashchanger.api.Currency.*
import com.cashchanger.cashchanger.api.Input
import com.cashchanger.cashchanger.dal.ExchangeRateEntity
import com.cashchanger.cashchanger.dal.ExchangeRatesDao
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDate
import org.mockito.Mockito.`when`


@RunWith(MockitoJUnitRunner::class)
class CashChangeServiceTest {

    val DATE: LocalDate = LocalDate.of(2001, 1, 1)
    val CZK_TO_EUR = 25.1
    val CZK_TO_GBP = 28.1
    val CZK_TO_JPY = 19.1
    val CZK_TO_USD = 21.1

    @InjectMocks
    lateinit var testee: CashChangeService

    @Mock
    lateinit var dao: ExchangeRatesDao

    @Before
    fun setUp() {
        `when`(dao.findTopByDateBetweenOrderByDateDesc(DATE.minusDays(5), DATE))
            .thenReturn(ExchangeRateEntity(DATE, CZK_TO_EUR, CZK_TO_GBP, CZK_TO_JPY, CZK_TO_USD))
    }

    @Test
    fun czkToUsdAndBackwardsEquals() {
        val starterCash = 100.0;
        val result1 = testee.calcAmount(Input(CZK, USD, starterCash, DATE))
        val result2 = testee.calcAmount(Input(USD, CZK, result1, DATE))
        assert(result2 == starterCash)
    }

    @Test
    fun czkToEurAndBackwardsEquals() {
        val starterCash = 100.0;
        val result1 = testee.calcAmount(Input(CZK, EUR, starterCash, DATE))
        val result2 = testee.calcAmount(Input(EUR, CZK, result1, DATE))
        assert(result2 == starterCash)
    }

    @Test
    fun czkToJpyAndBackwardsEquals() {
        val starterCash = 100.0;
        val result1 = testee.calcAmount(Input(CZK, JPY, starterCash, DATE))
        val result2 = testee.calcAmount(Input(JPY, CZK, result1, DATE))
        assert(result2 == starterCash)
    }

    @Test
    fun czkToGbpAndBackwardsEquals() {
        val starterCash = 100.0;
        val result1 = testee.calcAmount(Input(CZK, GBP, starterCash, DATE))
        val result2 = testee.calcAmount(Input(GBP, CZK, result1, DATE))
        assert(result2 == starterCash)
    }

    @Test
    fun usdToGbpAndBackwardsEquals() {
        val starterCash = 100.0;
        val result1 = testee.calcAmount(Input(USD, GBP, starterCash, DATE))
        val result2 = testee.calcAmount(Input(GBP, USD, result1, DATE))
        // ok I see where is the tricky part...
        assert(result2 % starterCash < 0.0000000000001)
    }

    @Test
    fun usdToEurAndBackwardsEquals() {
        val starterCash = 100.0;
        val result1 = testee.calcAmount(Input(USD, EUR, starterCash, DATE))
        val result2 = testee.calcAmount(Input(EUR, USD, result1, DATE))
        assert(result2 % starterCash < 0.0000000000001)
    }

    @Test
    fun ratesSeemsOk() {
        val result = testee.findRates(DATE)
        assert(
            result == """     CZK to EUR: 25.1
     CZK to GBP: 28.1
     CZK to JPY: 19.1
     CZK to USD: 21.1
     EUR to CZK: 0.0398406374501992
     EUR to GBP: 0.8932384341637011
     EUR to JPY: 1.3141361256544501
     EUR to USD: 1.1895734597156398
     GBP to CZK: 0.03558718861209964
     GBP to EUR: 1.1195219123505975
     GBP to JPY: 1.4712041884816753
     GBP to USD: 1.3317535545023695
     JPY to CZK: 0.05235602094240837
     JPY to EUR: 0.7609561752988048
     JPY to GPB: 0.6797153024911032
     JPY to USD: 0.9052132701421801
     USD to CZK: 0.04739336492890995
     USD to EUR: 0.8406374501992032
     USD to GPB: 0.7508896797153025
     USD to JPY: 1.1047120418848166"""
        )
    }
}
