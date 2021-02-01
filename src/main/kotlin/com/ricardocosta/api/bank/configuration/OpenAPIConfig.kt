package com.ricardocosta.api.bank.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.servers.Server
import org.springdoc.core.SpringDocUtils
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

@Component
@OpenAPIDefinition(
    info = Info(
        title = "Bank API",
        version = "1.0",
        description = "API documentation for Bank application."
    ),
    servers = [
        Server(url = "http://localhost:8080", description = "localhost")
    ]
)
class OpenAPIConfig {
    init {
        SpringDocUtils.getConfig().replaceWithClass(
            Pageable::class.java,
            org.springdoc.core.converters.models.Pageable::class.java
        )
    }
}
