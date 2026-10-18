package org.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyFinanceRegisterApplication

fun main(args: Array<String>) {
    runApplication<MyFinanceRegisterApplication>(*args)
}
