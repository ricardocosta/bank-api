package com.ricardocosta.api.bank.listeners

import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationContextInitializedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.ReactiveAdapterRegistry
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.BlockHoundIntegration
import reactor.blockhound.integration.ReactorIntegration
import reactor.blockhound.integration.StandardOutputIntegration
import reactor.core.scheduler.ReactorBlockHoundIntegration

class BlockHoundRegisterListener : ApplicationListener<ApplicationContextInitializedEvent> {

    private val logger = KotlinLogging.logger {}

    override fun onApplicationEvent(event: ApplicationContextInitializedEvent) {
        val context = event.applicationContext
        val isBlockhoundActive = context.environment.getProperty("blockhound.active", "false").toBoolean()

        if (isBlockhoundActive) {
            logger.info { "Activating BlockHound..." }

            BlockHound.builder()
                .with(ReactorIntegration())
                .with(StandardOutputIntegration())
                .with(ReactorBlockHoundIntegration())
                .with(ReactiveAdapterRegistry.SpringCoreBlockHoundIntegration())
                .with(BankAPIBlockHoundIntegration())
                .install()

            logger.info { "BlockHound activated!" }
        }
    }
}

class BankAPIBlockHoundIntegration : BlockHoundIntegration {
    override fun applyTo(builder: BlockHound.Builder) {
        builder.allowBlockingCallsInside(
            "kotlin.reflect.jvm.internal.impl.builtins.jvm.JvmBuiltInsPackageFragmentProvider",
            "findPackage"
        )
            .allowBlockingCallsInside(
                "org.hibernate.validator.resourceloading.PlatformResourceBundleLocator",
                "getResourceBundle"
            )
            .allowBlockingCallsInside(
                "java.util.UUID",
                "randomUUID"
            )
    }
}
