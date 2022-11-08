package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.service

import com.eventstore.dbclient.ConnectionShutdownException
import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.WriteResult
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.properties.EventStoreProperties
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import org.springframework.stereotype.Service

@Service
class EventStoreClient(eventStoreProperties: EventStoreProperties) {

    val connectionString =
        "esdb://${eventStoreProperties.host}:${eventStoreProperties.port}?tls=${eventStoreProperties.tls}"
    var eventStoreDBClient = eventStoreDBClient()

    fun readStream(
        streamName: String,
        maxCount: Long,
        readStreamOptions: ReadStreamOptions
    ): CompletableFuture<ReadResult> {
        val result = eventStoreDBClient.readStream(streamName, maxCount, readStreamOptions)
        if (result.isCompletedExceptionally) {
            eventStoreDBClient = eventStoreDBClient()
            return eventStoreDBClient.readStream(streamName, maxCount, readStreamOptions)
        }
        return result
    }

    fun appendToStream(streamName: String, event: EventData?): CompletableFuture<WriteResult> {
        val result = eventStoreDBClient.appendToStream(streamName, event)
        if (result.isCompletedExceptionally) {
            eventStoreDBClient = eventStoreDBClient()
            return eventStoreDBClient.appendToStream(streamName, event)
        }
        return result
    }

    private final fun eventStoreDBClient(): EventStoreDBClient {
        val settings: EventStoreDBClientSettings =
            EventStoreDBConnectionString.parseOrThrow(connectionString)!!

        return EventStoreDBClient.create(settings)
    }
}
