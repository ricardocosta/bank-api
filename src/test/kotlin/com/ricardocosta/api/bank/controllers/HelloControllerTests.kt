package com.ricardocosta.api.bank.controllers

import com.ricardocosta.api.bank.dto.HelloDTO
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [HelloController::class])
class HelloControllerTests {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Test
    fun `POST hello returns 405 Method Not Allowed`() {
        val helloDTO = HelloDTO("John Smith")

        webClient.post()
            .uri("/hello")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(helloDTO)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED)
    }
}
