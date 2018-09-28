package com.radiotelescope

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RadioTelescopeApplication

fun main(args: Array<String>) {
    runApplication<RadioTelescopeApplication>(*args)
}
