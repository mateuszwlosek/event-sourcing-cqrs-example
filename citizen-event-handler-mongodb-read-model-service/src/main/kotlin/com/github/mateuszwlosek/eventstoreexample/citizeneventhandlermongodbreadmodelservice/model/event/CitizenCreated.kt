package com.github.mateuszwlosek.eventstoreexample.citizeneventhandlermongodbreadmodelservice.model.event

import java.util.UUID

data class CitizenCreated(
    var eventId: UUID? = null,
    var citizenId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null
) : CitizenEvent
