package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.repository

import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.db.Citizen
import org.springframework.data.mongodb.repository.MongoRepository

interface CitizenRepository : MongoRepository<Citizen, String> {
    fun findByCitizenId(citizenId: String): Citizen?
}
