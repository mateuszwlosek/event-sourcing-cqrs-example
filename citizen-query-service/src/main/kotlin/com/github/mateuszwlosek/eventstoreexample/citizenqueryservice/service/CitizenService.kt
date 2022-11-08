package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.service

import com.eventstore.dbclient.ReadResult
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.StreamNotFoundException
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.exception.CitizenNotFoundException
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.db.Citizen
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.dto.CitizenAudit
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.properties.EventStoreProperties
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.repository.CitizenRepository
import java.util.concurrent.ExecutionException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CitizenService(
    private val client: EventStoreClient,
    private val repository: CitizenRepository,
    private val eventAuditMapper: CitizenEventAuditMapper,
    private val eventStoreProperties: EventStoreProperties
) {

    private val logger = LoggerFactory.getLogger(CitizenService::class.java)

    fun getCitizen(id: String): Citizen {
        logger.info("Looking for a citizen")
        return repository.findByCitizenId(id) ?: throw CitizenNotFoundException(id)
    }

    fun getCitizenAudit(citizenId: String): List<CitizenAudit> {
        logger.info("Constructing a citizen audit")

        val streamName = "${eventStoreProperties.citizensStreamPrefix}-$citizenId"

        val readStreamOptions = ReadStreamOptions.get()
            .backwards()
            .fromEnd()
        val eventsFuture = client.readStream(streamName, readStreamOptions)

        val events: ReadResult

        try {
            events = eventsFuture.get()
        } catch (exc: ExecutionException) {
            if (exc.cause is StreamNotFoundException) {
                throw CitizenNotFoundException(citizenId)
            }
            throw exc
        }

        return eventAuditMapper.mapEventsToAudits(events.events)
    }
}
