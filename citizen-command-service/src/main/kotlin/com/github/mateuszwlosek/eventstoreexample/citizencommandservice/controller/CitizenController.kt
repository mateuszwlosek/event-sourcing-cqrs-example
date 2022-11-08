package com.github.mateuszwlosek.eventstoreexample.citizencommandservice.controller

import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.model.dto.CitizenDTO
import com.github.mateuszwlosek.eventstoreexample.citizencommandservice.service.CitizenService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("citizens")
class CitizenController(val service: CitizenService) {

    @PostMapping
    fun createCitizen(@RequestBody citizen: CitizenDTO) {
        service.createCitizen(citizen)
    }

    @PutMapping
    fun updateCitizen(@RequestBody citizen: CitizenDTO) {
        service.updateCitizen(citizen)
    }

    @DeleteMapping
    fun deleteCitizen(@RequestParam citizenId: String) {
        service.deleteCitizen(citizenId)
    }
}
