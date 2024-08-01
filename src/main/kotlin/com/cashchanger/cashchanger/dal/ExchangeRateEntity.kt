package com.cashchanger.cashchanger.dal

import java.time.LocalDate
import javax.persistence.*

@Entity
data class ExchangeRateEntity(

    @Column
    val date: LocalDate,

    @Column
    val czkToEur: Double,

    @Column
    val czkToGbp: Double,

    @Column
    val czkToJpy: Double,

    @Column
    val czkToUsd: Double
) {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}