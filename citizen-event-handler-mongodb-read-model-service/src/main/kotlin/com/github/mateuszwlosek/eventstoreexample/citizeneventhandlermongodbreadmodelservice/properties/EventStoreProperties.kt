package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "citizen-service.event-store")
data class EventStoreProperties(
    val host: String,
    val port: String,
    val citizensStreamPrefix: String,
    val citizenCreatedEvent: String,
    val citizenUpdatedEvent: String,
    val citizenDeletedEvent: String,
    val tls: Boolean
)
