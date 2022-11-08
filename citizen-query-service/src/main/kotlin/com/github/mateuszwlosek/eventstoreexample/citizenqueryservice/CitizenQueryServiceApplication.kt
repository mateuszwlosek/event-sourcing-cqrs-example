package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CitizenQueryServiceApplication

fun main(args: Array<String>) {
    runApplication<CitizenQueryServiceApplication>(*args)
}
