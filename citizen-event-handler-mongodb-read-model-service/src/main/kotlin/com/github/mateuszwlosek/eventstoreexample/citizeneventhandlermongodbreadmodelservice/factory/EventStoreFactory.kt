package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.factory

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.properties.EventStoreProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EventStoreFactory(eventStoreProperties: EventStoreProperties) {

    val connectionString = "esdb://${eventStoreProperties.host}:${eventStoreProperties.port}?tls=${eventStoreProperties.tls}"

    @Bean
    fun eventStoreDBPersistentSubscriptionsClient(): EventStoreDBPersistentSubscriptionsClient? {
        val settings: EventStoreDBClientSettings =
            EventStoreDBConnectionString.parseOrThrow(connectionString)!!

        return EventStoreDBPersistentSubscriptionsClient.createToStream(settings)
    }
}
