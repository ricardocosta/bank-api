package com.ricardocosta.template.listeners

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationContextInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.ReactiveAdapterRegistry
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.ReactorIntegration
import reactor.blockhound.integration.StandardOutputIntegration
import reactor.core.scheduler.ReactorBlockHoundIntegration

class BlockHoundRegisterListener : ApplicationListener<ApplicationContextInitializedEvent> {

    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ApplicationContextInitializedEvent) {
        val context = event.applicationContext
        val isBlockhoundActive = context.environment.getProperty("blockhound.active", "false").toBoolean()

        if (isBlockhoundActive) {
            logger.debug { "Activating BlockHound..." }

            BlockHound.builder()
                .with(ReactorIntegration())
                .with(StandardOutputIntegration())
                .with(ReactorBlockHoundIntegration())
                .with(ReactiveAdapterRegistry.SpringCoreBlockHoundIntegration())
                .allowBlockingCallsInside(
                    "com.fasterxml.jackson.module.kotlin.KotlinNamesAnnotationIntrospector",
                    "findKotlinParameterName"
                )
                .install()

            logger.debug { "BlockHound activated!" }
        }
    }
}
