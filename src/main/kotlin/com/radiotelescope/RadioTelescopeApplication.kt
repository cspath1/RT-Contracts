package com.radiotelescope

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class RadioTelescopeApplication

/**
 * Main method in charge of booting up the Spring Application
 */
fun main(args: Array<String>) {
    runApplication<RadioTelescopeApplication>(*args)
}
