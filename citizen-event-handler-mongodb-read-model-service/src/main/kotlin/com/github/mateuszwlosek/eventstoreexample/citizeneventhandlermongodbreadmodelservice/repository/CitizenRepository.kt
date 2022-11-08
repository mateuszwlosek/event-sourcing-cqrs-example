package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.repository

import com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.db.Citizen
import org.springframework.data.mongodb.repository.MongoRepository

interface CitizenRepository : MongoRepository<Citizen, String> {
    fun findByCitizenId(citizenId: String): Citizen?
    fun deleteByCitizenId(citizenId: String)
}
