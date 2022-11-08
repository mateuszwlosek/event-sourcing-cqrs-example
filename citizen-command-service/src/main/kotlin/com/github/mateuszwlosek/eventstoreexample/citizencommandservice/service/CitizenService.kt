package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.service

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.StreamNotFoundException
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.exception.BadRequestException
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.exception.DelayedEventHandlerException
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.db.Citizen
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.dto.CitizenDTO
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.event.CitizenCreated
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.event.CitizenDeleted
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.event.CitizenUpdated
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.properties.EventStoreProperties
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.repository.CitizenRepository
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.ExecutionException
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional

@Service
class CitizenService(
    private val eventStoreClient: EventStoreClient,
    private val repository: CitizenRepository,
    private val eventStoreProperties: EventStoreProperties
) {

    private val logger = LoggerFactory.getLogger(CitizenService::class.java)

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun createCitizen(citizen: CitizenDTO) {
        logger.info("Request to create a citizen: $citizen")

        val citizenId = citizen.citizenId
        val streamName = "${eventStoreProperties.citizensStreamPrefix}-$citizenId"

        val eventId = UUID.randomUUID()
        val citizenCreated = CitizenCreated(
            eventId,
            citizenId,
            citizen.firstName,
            citizen.lastName,
            citizen.address
        )

        val doesCitizenExistAlready = getCitizen(citizenId) != null
        if (doesCitizenExistAlready) {
            throw BadRequestException("Citizen already exists in the database!")
        }

        val doesStreamAlreadyExist = doesStreamExist(streamName)
        if (doesStreamAlreadyExist) {
            throw BadRequestException("Citizen was already requested to be created!")
        }

        val event = EventData
            .builderAsJson(eventStoreProperties.citizenCreatedEvent, citizenCreated)
            .build()

        eventStoreClient
            .appendToStream(streamName, event)
            .get()
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun updateCitizen(citizen: CitizenDTO) {
        logger.info("Request to update citizen: $citizen")

        val citizenId = citizen.citizenId
        val streamName = "${eventStoreProperties.citizensStreamPrefix}-$citizenId"

        val citizenEntity = getCitizen(citizen.citizenId)
        citizenEntity ?: throw BadRequestException("Citizen with id: $citizenId does not exist!")

        val eventId = UUID.randomUUID()
        val citizenUpdated = CitizenUpdated(
            eventId,
            citizenId,
            citizen.firstName,
            citizen.lastName,
            citizen.address
        )

        checkIfReadModelIsUpToDateWithRetrying(citizenId, streamName)

        val event = EventData
            .builderAsJson(eventStoreProperties.citizenUpdatedEvent, citizenUpdated)
            .build()

        eventStoreClient
            .appendToStream(streamName, event)
            .get()
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    fun deleteCitizen(citizenId: String) {
        logger.info("Request to delete a citizen by a citizen id: $citizenId")

        val streamName = "${eventStoreProperties.citizensStreamPrefix}-$citizenId"
        val citizenEntity = getCitizen(citizenId)

        citizenEntity ?: throw BadRequestException("Citizen with id: $citizenId does not exist!")

        val eventId = UUID.randomUUID()
        val citizenDeleted = CitizenDeleted(
            eventId,
            citizenId,
        )

        checkIfReadModelIsUpToDateWithRetrying(citizenId, streamName)

        val event = EventData
            .builderAsJson(eventStoreProperties.citizenDeletedEvent, citizenDeleted)
            .build()

        eventStoreClient
            .appendToStream(streamName, event)
            .get()
    }

    private fun getCitizen(citizenId: String): Citizen? {
        logger.info("Looking for a citizen by a citizen id: $citizenId")
        return repository.findByCitizenId(citizenId)
    }

    private fun checkIfReadModelIsUpToDateWithRetrying(citizenId: String, streamName: String) {
        val maxAmountOfRetries = 3
        val delayBetweenRetriesInSeconds = 3L

        for (retry in 0..maxAmountOfRetries) {
            try {
                checkIfReadModelIsUpToDate(citizenId, streamName)
                return
            } catch (exc: DelayedEventHandlerException) {
                logger.info("Event Handler hasn't caught up to the event stream yet!")
                Thread.sleep(delayBetweenRetriesInSeconds * 1000)
            } catch (exc: ExecutionException) {
                if (exc.cause is StreamNotFoundException) {
                    logger.info("Stream: $streamName does not exist yet. No point in checking if event handler is up to date")
                    return
                }
                throw exc
            } catch (exc: Exception) {
                throw exc
            }
        }

        throw DelayedEventHandlerException()
    }

    private fun checkIfReadModelIsUpToDate(citizenId: String, streamName: String) {
        val readStreamOptions = ReadStreamOptions.get()
            .backwards()
            .fromEnd()
        val streamEventsFuture = eventStoreClient.readStream(streamName, 1, readStreamOptions)

        val events = streamEventsFuture.get().events
        if (events.isEmpty()) {
            return
        }

        val currentReadModelCitizen = getCitizen(citizenId)
        if (currentReadModelCitizen != null) {
            val currentDatabaseRevision = currentReadModelCitizen.commit
            val currentRevision = events[0].originalEvent.eventId.toString()

            if (!currentDatabaseRevision.equals(currentRevision)) {
                throw DelayedEventHandlerException()
            }
        }
    }

    private fun doesStreamExist(streamName: String): Boolean {
        val readStreamOptions = ReadStreamOptions.get().backwards().fromEnd()

        return try {
            eventStoreClient.readStream(streamName, 1, readStreamOptions).get()
            true
        } catch (exc: ExecutionException) {
            if (exc.cause is StreamNotFoundException) {
                return false
            }
            throw exc
        }
    }
}
