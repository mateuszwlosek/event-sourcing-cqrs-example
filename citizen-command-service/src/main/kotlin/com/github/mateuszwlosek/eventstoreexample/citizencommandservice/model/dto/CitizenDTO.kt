package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.dto

data class CitizenDTO(
    var citizenId: String,
    val firstName: String,
    val lastName: String,
    val address: String
)
