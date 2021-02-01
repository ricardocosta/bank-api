package com.ricardocosta.template.listeners

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.RETURNS_SELF
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationContextInitializedEvent
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.ReactiveAdapterRegistry
import org.springframework.mock.env.MockEnvironment
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.blockhound.BlockHound
import reactor.blockhound.integration.ReactorIntegration
import reactor.blockhound.integration.StandardOutputIntegration
import reactor.core.scheduler.ReactorBlockHoundIntegration

@ExtendWith(SpringExtension::class)
class BlockHoundRegisterListenerTests {

    @Test
    fun `onApplicationEvent installs BlockHound when it is active`() {
        // Setup the environment
        val mockEnvironment = MockEnvironment()
        mockEnvironment.setProperty("blockhound.active", "true")

        // Setup the application
        val application = SpringApplication()
        val args = arrayOfNulls<String>(1)
        val applicationContext = GenericApplicationContext().apply {
            environment = mockEnvironment
        }
        val event = ApplicationContextInitializedEvent(application, args, applicationContext)

        // Setup BlockHound Builder mock
        val blockHoundBuilderMock = mock(BlockHound.Builder::class.java, RETURNS_SELF)
        `when`(blockHoundBuilderMock.with(any())).doReturn(blockHoundBuilderMock)
        `when`(
            blockHoundBuilderMock.allowBlockingCallsInside(anyString(), anyString())
        ).thenReturn(blockHoundBuilderMock)

        // Setup BlockHound mock
        val blockHoundMock = mockStatic(BlockHound::class.java)
        blockHoundMock.`when`<BlockHound.Builder>(BlockHound::builder).thenReturn(blockHoundBuilderMock)

        BlockHoundRegisterListener().onApplicationEvent(event)

        // Check if integrations are setup
        verify(blockHoundBuilderMock, times(1)).with(any(ReactorIntegration::class.java))
        verify(blockHoundBuilderMock, times(1)).with(any(StandardOutputIntegration::class.java))
        verify(blockHoundBuilderMock, times(1)).with(any(ReactorBlockHoundIntegration::class.java))
        verify(
            blockHoundBuilderMock,
            times(1)
        ).with(any(ReactiveAdapterRegistry.SpringCoreBlockHoundIntegration::class.java))

        // Check if exclusions are setup
        verify(blockHoundBuilderMock, times(1)).allowBlockingCallsInside(
            "com.fasterxml.jackson.module.kotlin.KotlinNamesAnnotationIntrospector",
            "findKotlinParameterName"
        )

        // Check if BlockHound is installed
        verify(blockHoundBuilderMock, times(1)).install()

        blockHoundMock.close()
    }

    @Test
    fun `onApplicationEvent does not install BlockHound when it is not active`() {
        // Setup the environment
        val mockEnvironment = MockEnvironment()
        mockEnvironment.setProperty("blockhound.active", "false")

        // Setup the application
        val application = SpringApplication()
        val args = arrayOfNulls<String>(1)
        val applicationContext = GenericApplicationContext().apply {
            environment = mockEnvironment
        }
        val event = ApplicationContextInitializedEvent(application, args, applicationContext)

        // Setup BlockHound mock
        val blockHoundMock = mockStatic(BlockHound::class.java)

        BlockHoundRegisterListener().onApplicationEvent(event)

        blockHoundMock.verifyNoInteractions()

        blockHoundMock.close()
    }
}
