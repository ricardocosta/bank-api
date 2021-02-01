package com.ricardocosta.api.bank.exceptions

import com.ricardocosta.api.bank.dto.ErrorDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ExtendWith(MockitoExtension::class)
class ExceptionHandlersTests {

    @InjectMocks
    private lateinit var exceptionHandler: ExceptionHandlers

    @Test
    fun `handleNotImplementedError returns E000 error`() {
        val ex = NotImplementedError()

        val result = exceptionHandler.handleNotImplementedError(ex)
        val expected = ResponseEntity<ErrorDTO>(
            ErrorDTO("E000", message = "Not implemented yet."),
            HttpStatus.METHOD_NOT_ALLOWED
        )

        assertEquals(expected, result)
    }

    @Test
    fun `handleRuntimeException returns E001 error`() {
        val ex = RuntimeException("Oops, something went wrong.")

        val result = exceptionHandler.handleRuntimeException(ex)
        val expected = ResponseEntity<ErrorDTO>(
            ErrorDTO("E001", message = "Unexpected error has occurred."),
            HttpStatus.INTERNAL_SERVER_ERROR
        )

        assertEquals(expected, result)
    }
}
