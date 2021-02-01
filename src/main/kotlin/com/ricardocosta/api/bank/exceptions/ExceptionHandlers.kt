package com.ricardocosta.api.bank.exceptions

import com.ricardocosta.api.bank.dto.ErrorDTO
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ExceptionHandlers {

    private val logger = KotlinLogging.logger {}

    companion object {
        const val NOT_IMPLEMENTED_CODE = "E000"
        const val NOT_IMPLEMENTED_MSG = "Not implemented yet."

        const val UNEXPECTED_CODE = "E001"
        const val UNEXPECTED_MSG = "Unexpected error has occurred."
    }

    @ExceptionHandler(NotImplementedError::class)
    @ApiResponse(
        responseCode = "405",
        description = "Not implemented.",
        content = [
            Content(
                schema = Schema(implementation = ErrorDTO::class),
                examples = [
                    ExampleObject("{\"code\": \"$NOT_IMPLEMENTED_CODE\", \"message\": \"$NOT_IMPLEMENTED_MSG\"}"),
                ]
            )
        ]
    )
    fun handleNotImplementedError(error: NotImplementedError): ResponseEntity<ErrorDTO> {
        logger.warn { error }

        return ResponseEntity(
            ErrorDTO(NOT_IMPLEMENTED_CODE, message = NOT_IMPLEMENTED_MSG),
            HttpStatus.METHOD_NOT_ALLOWED
        )
    }

    @ApiResponse(
        responseCode = "500",
        description = "Unexpected error.",
        content = [
            Content(
                schema = Schema(implementation = ErrorDTO::class),
                examples = [
                    ExampleObject("{\"code\": \"$UNEXPECTED_CODE\", \"message\": \"$UNEXPECTED_MSG\"}"),
                ]
            )
        ]
    )
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<ErrorDTO> {
        logger.warn { exception }

        return ResponseEntity(
            ErrorDTO(UNEXPECTED_CODE, message = UNEXPECTED_MSG),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}
