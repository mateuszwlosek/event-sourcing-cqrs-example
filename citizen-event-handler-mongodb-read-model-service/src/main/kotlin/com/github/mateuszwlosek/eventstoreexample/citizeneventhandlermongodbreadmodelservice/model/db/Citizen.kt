package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.db

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("citizen")
data class Citizen(
    @Id var id: String? = null,
    var commit: String? = null,
    var citizenId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null,
)
