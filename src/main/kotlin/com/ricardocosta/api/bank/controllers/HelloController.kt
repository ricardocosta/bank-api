package com.ricardocosta.api.bank.controllers

import com.ricardocosta.api.bank.dto.HelloDTO
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import mu.KotlinLogging
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/hello", consumes = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "Hello")
class HelloController {
    private val logger = KotlinLogging.logger {}

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        description = "Description of what POST /hello does.",
        summary = "Summary of the operation",
        responses = [
            ApiResponse(responseCode = "201", description = "Created.")
        ]
    )
    fun hello(@RequestBody helloDTO: HelloDTO): String {
        logger.debug { "POST /hello was called with $helloDTO" }

        throw NotImplementedError("TO DO")
    }
}
