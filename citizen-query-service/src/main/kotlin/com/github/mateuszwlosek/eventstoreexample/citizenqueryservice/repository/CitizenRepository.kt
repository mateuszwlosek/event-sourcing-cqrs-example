package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.repository

import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.db.Citizen
import org.springframework.data.mongodb.repository.MongoRepository

interface CitizenRepository : MongoRepository<Citizen, String> {
    fun findByCitizenId(citizenId: String): Citizen?
}
