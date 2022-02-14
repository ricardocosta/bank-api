package com.ricardocosta.api.bank.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.ricardocosta.api.bank.domain.User
import java.util.UUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@JsonIgnoreProperties(ignoreUnknown = true)
data class CreateUserDTO(
    @get:NotBlank
    @get:Size(max = 128, min = 1)
    val username: String
) {
    companion object {
        fun toEntity(dto: CreateUserDTO) = User(
            username = dto.username,
            publicId = UUID.randomUUID().toString()
        )
    }
}
