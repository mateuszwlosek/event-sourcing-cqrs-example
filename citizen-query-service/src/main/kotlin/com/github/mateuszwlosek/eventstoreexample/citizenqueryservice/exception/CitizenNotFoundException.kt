package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_FOUND)
class CitizenNotFoundException(citizenId: String): RuntimeException("Citizen with an id: $citizenId was not found")
