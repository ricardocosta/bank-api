package com.ricardocosta.api.bank.controllers

import com.ricardocosta.api.bank.domain.views.UserDetailsView
import com.ricardocosta.api.bank.dto.CreateUserDTO
import com.ricardocosta.api.bank.interactors.CreateUserInteractor
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/users", consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Users")
class UsersController(
    private val createUserInteractor: CreateUserInteractor
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        description = "Create a new user with the provided details.",
        summary = "Create User",
        responses = [
            ApiResponse(responseCode = "201", description = "Created.")
        ]
    )
    fun create(@RequestBody @Valid dto: CreateUserDTO): Mono<UserDetailsView> {
        logger.info { "This is the DTO $dto" }

        return createUserInteractor.call(dto)
    }
}
