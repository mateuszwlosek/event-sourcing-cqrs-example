package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.event

import java.util.UUID

data class CitizenUpdated(
    var id: UUID? = null,
    var citizenId: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var address: String? = null
) : CitizenEvent
