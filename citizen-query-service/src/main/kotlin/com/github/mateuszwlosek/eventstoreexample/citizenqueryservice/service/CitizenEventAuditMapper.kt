package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.service

import com.eventstore.dbclient.ResolvedEvent
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.dto.CitizenAudit
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.event.CitizenCreated
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.event.CitizenDeleted
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.event.CitizenUpdated
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.properties.EventStoreProperties
import java.lang.IllegalArgumentException
import org.springframework.stereotype.Service

@Service
class CitizenEventAuditMapper(private val eventStoreProperties: EventStoreProperties) {

    fun mapEventsToAudits(events: MutableList<ResolvedEvent>): List<CitizenAudit> {
        return events.map { mapEventToAudit(it) }
    }

    fun mapEventToAudit(event: ResolvedEvent): CitizenAudit {
        return when (event.originalEvent.eventType) {
            eventStoreProperties.citizenCreatedEvent -> {
                val citizenCreated = event.originalEvent.getEventDataAs(CitizenCreated::class.java)
                CitizenAudit(citizenCreated.citizenId!!, "Created a new citizen", citizenCreated)
            }
            eventStoreProperties.citizenUpdatedEvent -> {
                val citizenUpdated = event.originalEvent.getEventDataAs(CitizenUpdated::class.java)
                CitizenAudit(citizenUpdated.citizenId!!, "Updated a citizen", citizenUpdated)
            }
            eventStoreProperties.citizenDeletedEvent -> {
                val citizenDeleted = event.originalEvent.getEventDataAs(CitizenDeleted::class.java)
                CitizenAudit(citizenDeleted.citizenId!!, "Deleted a citizen", citizenDeleted)
            }
            else -> throw IllegalArgumentException("Unknown event type: ${event.originalEvent.eventType}")
        }
    }
}
