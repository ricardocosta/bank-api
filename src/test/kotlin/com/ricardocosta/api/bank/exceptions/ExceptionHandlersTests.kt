package com.ricardocosta.api.bank.exceptions

import com.nhaarman.mockitokotlin2.doReturn
import com.ricardocosta.api.bank.dto.ErrorDTO
import com.ricardocosta.api.bank.dto.ValidationErrorDTO
import com.ricardocosta.api.bank.dto.ValidationErrorDetailsDTO
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@ExtendWith(MockitoExtension::class)
class ExceptionHandlersTests {

    @InjectMocks
    private lateinit var exceptionHandler: ExceptionHandlers

    @Test
    fun `#handleWebExchangeBindException - Size failure returns E002 error with E002-2 validation error`() {
        // Mocking the error, as it's quite harder to create it from scratch
        val webExchangeBindExceptionMock = Mockito.mock(WebExchangeBindException::class.java, Mockito.RETURNS_SELF)
        `when`(webExchangeBindExceptionMock.fieldErrors).doReturn(
            listOf(
                FieldError(
                    "createUserDTO",
                    "username",
                    null,
                    false,
                    arrayOf("Size"),
                    null,
                    null
                )
            )
        )

        val result = exceptionHandler.handleWebExchangeBindException(webExchangeBindExceptionMock)
        val expected = ResponseEntity<ValidationErrorDTO>(
            ValidationErrorDTO(
                "E002",
                "Bad Request.",
                listOf(
                    ValidationErrorDetailsDTO("E002-2", "username", "N/A")
                )
            ),
            HttpStatus.BAD_REQUEST
        )

        assertEquals(expected, result)
    }

    @Test
    fun `#handleWebExchangeBindException - NotBlank failure returns E002 error with E002-1 validation error`() {
        // Mocking the error, as it's quite harder to create it from scratch
        val webExchangeBindExceptionMock = Mockito.mock(WebExchangeBindException::class.java, Mockito.RETURNS_SELF)
        `when`(webExchangeBindExceptionMock.fieldErrors).doReturn(
            listOf(
                FieldError(
                    "createUserDTO",
                    "username",
                    null,
                    false,
                    arrayOf("NotBlank"),
                    null,
                    "must not be blank"
                )
            )
        )

        val result = exceptionHandler.handleWebExchangeBindException(webExchangeBindExceptionMock)
        val expected = ResponseEntity<ValidationErrorDTO>(
            ValidationErrorDTO(
                "E002",
                "Bad Request.",
                listOf(
                    ValidationErrorDetailsDTO("E002-1", "username", "must not be blank")
                )
            ),
            HttpStatus.BAD_REQUEST
        )

        assertEquals(expected, result)
    }

    @Test
    fun `#handleServerWebInputException - returns E002 error`() {
        val ex = ServerWebInputException("Oops, something went wrong.")

        val result = exceptionHandler.handleServerWebInputException(ex)
        val expected = ResponseEntity<ErrorDTO>(
            ErrorDTO("E002", message = "Bad Request."),
            HttpStatus.BAD_REQUEST
        )

        assertEquals(expected, result)
    }

    @Test
    fun `#handleNotImplementedError - returns E000 error`() {
        val ex = NotImplementedError()

        val result = exceptionHandler.handleNotImplementedError(ex)
        val expected = ResponseEntity<ErrorDTO>(
            ErrorDTO("E000", message = "Not implemented yet."),
            HttpStatus.METHOD_NOT_ALLOWED
        )

        assertEquals(expected, result)
    }

    @Test
    fun `#handleRuntimeException - returns E001 error`() {
        val ex = RuntimeException("Oops, something went wrong.")

        val result = exceptionHandler.handleRuntimeException(ex)
        val expected = ResponseEntity<ErrorDTO>(
            ErrorDTO("E001", message = "Unexpected error has occurred."),
            HttpStatus.INTERNAL_SERVER_ERROR
        )

        assertEquals(expected, result)
    }
}
