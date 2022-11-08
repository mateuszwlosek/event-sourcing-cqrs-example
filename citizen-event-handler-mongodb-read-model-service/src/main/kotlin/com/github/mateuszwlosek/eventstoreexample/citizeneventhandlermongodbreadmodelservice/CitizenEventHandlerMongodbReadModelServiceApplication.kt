package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class CitizenEventHandlerMongodbReadModelServiceApplication

fun main(args: Array<String>) {
    runApplication<CitizenEventHandlerMongodbReadModelServiceApplication>(*args)
}
