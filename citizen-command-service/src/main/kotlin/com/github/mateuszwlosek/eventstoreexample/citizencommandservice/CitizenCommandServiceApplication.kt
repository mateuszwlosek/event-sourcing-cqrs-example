package com.github.mateuszwlosek.eventstoreexample.citizencommandservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CitizenCommandServiceApplication

fun main(args: Array<String>) {
    runApplication<CitizenCommandServiceApplication>(*args)
}
