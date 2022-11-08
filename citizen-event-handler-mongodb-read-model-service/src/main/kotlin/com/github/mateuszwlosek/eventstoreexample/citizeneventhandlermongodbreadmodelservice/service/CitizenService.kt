package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.service

import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.listener.CitizenEventListener
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.db.Citizen
import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.repository.CitizenRepository
import java.lang.IllegalArgumentException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CitizenService(
    private val repository: CitizenRepository
) {

    private val logger = LoggerFactory.getLogger(CitizenService::class.java)

    fun createCitizen(citizen: Citizen) {
        val existingCitizen = findCitizen(citizen.citizenId!!)

        if (existingCitizen != null) {
            if (existingCitizen.commit == citizen.commit) {
                logger.info("Citizen with the same commit already exists in the database, skipping the database insert")
                return
            }
            throw IllegalArgumentException("Citizen with the same id already exists in the database but has a different commit")
        }

        val savedCitizen = repository.save(citizen)
        logger.info("Saved a new citizen: $savedCitizen")
    }

    fun updateCitizen(citizen: Citizen) {
        val existingCitizen = repository.findByCitizenId(citizen.citizenId!!)!!

        existingCitizen.commit = citizen.commit
        existingCitizen.citizenId = citizen.citizenId
        existingCitizen.firstName = citizen.firstName
        existingCitizen.lastName = citizen.lastName
        existingCitizen.address = citizen.address

        val updatedCitizen = repository.save(existingCitizen)
        logger.info("Updated a citizen: $updatedCitizen")
    }

    fun deleteCitizen(citizenId: String) {
        repository.deleteByCitizenId(citizenId)
        logger.info("Deleted a citizen with an id: $citizenId")
    }

    private fun findCitizen(citizenId: String): Citizen? {
        logger.info("Looking for a citizen by a citizen id: $citizenId")
        return repository.findByCitizenId(citizenId)
    }
}
