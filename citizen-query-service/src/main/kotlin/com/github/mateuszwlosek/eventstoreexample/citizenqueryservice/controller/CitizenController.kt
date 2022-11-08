package com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.controller

import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.dto.CitizenAudit
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.model.dto.CitizenDTO
import com.github.mateuszwlosek.eventstoreexample.citizenqueryservice.service.CitizenService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("citizens")
class CitizenController(val service: CitizenService) {

    @GetMapping
    fun getCitizen(@RequestParam citizenId: String): CitizenDTO? {
        val citizen = service.getCitizen(citizenId)

        return CitizenDTO(
            citizen.citizenId!!,
            citizen.firstName!!,
            citizen.lastName!!,
            citizen.address!!
        )
    }
}
