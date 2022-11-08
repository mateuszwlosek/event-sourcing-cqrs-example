package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.event

import java.util.UUID

data class CitizenDeleted(
    var eventId: UUID? = null,
    var citizenId: String? = null
) : CitizenEvent
