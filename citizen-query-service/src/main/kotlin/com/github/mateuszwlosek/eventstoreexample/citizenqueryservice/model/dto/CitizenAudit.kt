package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.dto

import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.event.CitizenEvent

data class CitizenAudit(
    val citizenId: String,
    val action: String,
    val citizenEvent: CitizenEvent
)
