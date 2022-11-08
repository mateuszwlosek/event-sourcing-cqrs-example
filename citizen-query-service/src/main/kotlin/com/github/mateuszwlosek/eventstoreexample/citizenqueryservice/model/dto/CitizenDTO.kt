package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.dto

data class CitizenDTO(
    var citizenId: String,
    val firstName: String,
    val lastName: String,
    val address: String
)
