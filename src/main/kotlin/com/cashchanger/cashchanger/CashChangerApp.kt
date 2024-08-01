package com.cashchanger.cashchanger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CashChangerApp

fun main(args: Array<String>) {
	runApplication<CashChangerApp>(*args)
}

