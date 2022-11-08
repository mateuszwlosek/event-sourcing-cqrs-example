package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.listener

import com.eventstore.dbclient.CreatePersistentSubscriptionToAllOptions
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.eventstore.dbclient.PersistentSubscription
import com.eventstore.dbclient.PersistentSubscriptionListener
import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.SubscriptionFilterBuilder
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.db.Citizen
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.event.CitizenCreated
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.event.CitizenDeleted
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.event.CitizenUpdated
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.properties.EventStoreProperties
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.repository.CitizenRepository
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.service.CitizenService
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value

@Service
class CitizenEventListener(
    private val client: EventStoreDBPersistentSubscriptionsClient,
    private val citizenService: CitizenService,
    private val eventStoreProperties: EventStoreProperties,
    @Value("\${spring.application.name}") private val applicationName: String
) {

    private val logger = LoggerFactory.getLogger(CitizenEventListener::class.java)

    @PostConstruct
    fun registerListener() {
        val listener: PersistentSubscriptionListener = object : PersistentSubscriptionListener() {
            override fun onEvent(subscription: PersistentSubscription, event: ResolvedEvent) {
                try {
                    handleEvent(event)
                    subscription.ack(event)
                } catch (exc: Exception) {
                    logger.info("Problem while consuming an event happened", exc)
                }
            }
        }

        val subscriptionFilter = SubscriptionFilterBuilder()
            .withStreamNameRegularExpression("^${eventStoreProperties.citizensStreamPrefix}.*")
            .build()

        val options = CreatePersistentSubscriptionToAllOptions.get()
            .fromStart()
            .filter(subscriptionFilter)

        client.createToAll(applicationName, options)
        val result = client.subscribeToAll(applicationName, listener)

        if (result.isCompletedExceptionally) {
            throw Exception("Could not subscribe to the stream")
        }
    }

    private fun handleEvent(event: ResolvedEvent) {
        logger.info(
            "Received event" +
                event.originalEvent.streamRevision +
                "@" + event.originalEvent.streamId +
                "@" + event.originalEvent.position.toString()
        )

        when (event.originalEvent.eventType) {
            eventStoreProperties.citizenCreatedEvent -> handleCitizenCreated(
                event.originalEvent.getEventDataAs(CitizenCreated::class.java),
                event.originalEvent.eventId.toString()
            )
            eventStoreProperties.citizenUpdatedEvent -> handleCitizenUpdated(
                event.originalEvent.getEventDataAs(CitizenUpdated::class.java),
                event.originalEvent.eventId.toString()
            )
            eventStoreProperties.citizenDeletedEvent -> handleCitizenDeleted(
                event.originalEvent.getEventDataAs(CitizenDeleted::class.java)
            )
        }
    }

    private fun handleCitizenCreated(event: CitizenCreated, commit: String) {
        logger.info("Received citizen created event event: $event")

        val citizen = Citizen(id = null, commit, event.citizenId, event.firstName, event.lastName, event.address)
        citizenService.createCitizen(citizen)
    }

    private fun handleCitizenUpdated(event: CitizenUpdated, commit: String) {
        logger.info("Received citizen updated event: $event")

        val citizen = Citizen(id = null, commit, event.citizenId, event.firstName, event.lastName, event.address)
        citizenService.updateCitizen(citizen)
    }

    private fun handleCitizenDeleted(event: CitizenDeleted) {
        logger.info("Received citizen deleted event: $event")

        citizenService.deleteCitizen(event.citizenId!!)
    }
}
