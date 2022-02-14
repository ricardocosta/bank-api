@file:Suppress("UtilityClassWithPublicConstructor")

package com.ricardocosta.api.bank

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
open class ContainerBackedTest {
    companion object {
        @Container
        val container = PostgreSQLContainer<Nothing>("postgres:13.3-alpine").apply {
            withDatabaseName("bank")
            withUsername("bank_api")
            withPassword("s3crEt")
        }

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            val getContainerURL = {
                "r2dbc:postgresql://${container.host}:${container.firstMappedPort}/${container.databaseName}"
            }

            registry.add("spring.r2dbc.url", getContainerURL)
            registry.add("spring.r2dbc.password", container::getPassword)
            registry.add("spring.r2dbc.user", container::getUsername)

            registry.add("spring.flyway.url", container::getJdbcUrl)
            registry.add("spring.flyway.password", container::getPassword)
            registry.add("spring.flyway.user", container::getUsername)
        }
    }
}
