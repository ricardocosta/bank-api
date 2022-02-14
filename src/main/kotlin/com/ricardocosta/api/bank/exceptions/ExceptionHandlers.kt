package com.ricardocosta.api.bank.exceptions

import com.ricardocosta.api.bank.dto.ErrorDTO
import com.ricardocosta.api.bank.dto.ValidationErrorDTO
import com.ricardocosta.api.bank.dto.ValidationErrorDetailsDTO
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException

@ControllerAdvice
class ExceptionHandlers {

    private val logger = KotlinLogging.logger {}

    companion object {
        const val NOT_IMPLEMENTED_CODE = "E000"
        const val NOT_IMPLEMENTED_MSG = "Not implemented yet."

        const val UNEXPECTED_CODE = "E001"
        const val UNEXPECTED_MSG = "Unexpected error has occurred."

        const val BAD_REQUEST_CODE = "E002"
        const val BAD_REQUEST_MSG = "Bad Request."

        val validationErrorCodes = mapOf(
            "NotBlank" to "E002-1",
            "Size" to "E002-2"
        )
    }

    @ApiResponse(
        responseCode = "400",
        description = "Bad Request.",
        content = [
            Content(
                schema = Schema(implementation = ValidationErrorDTO::class),
                examples = [
                    ExampleObject(
                        "{" +
                            "\"code\": \"$BAD_REQUEST_CODE\"," +
                            "\"message\": \"$BAD_REQUEST_MSG\"," +
                            "\"validationErrorDetails\": [" +
                            "{" +
                            "\"code\": \"E002.1\"," +
                            "\"field\": \"username\"," +
                            "\"message\": \"must not be blank\"" +
                            "}" +
                            "]" +
                            "}"
                    ),
                ]
            )
        ]
    )
    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(error: WebExchangeBindException): ResponseEntity<ValidationErrorDTO> {
        try {
            val validationErrors = error.fieldErrors.flatMap {
                listOf(
                    ValidationErrorDetailsDTO(
                        code = validationErrorCodes.getOrDefault(it.code, "N/A"),
                        field = it.field,
                        message = it.defaultMessage ?: "N/A"
                    )
                )
            }

            return ResponseEntity(
                ValidationErrorDTO(
                    code = BAD_REQUEST_CODE,
                    message = BAD_REQUEST_MSG,
                    validationErrorDetails = validationErrors
                ),
                HttpStatus.BAD_REQUEST
            )
        } catch (ex: java.lang.RuntimeException) {
            logger.error { ex }
            throw ex
        }
    }

    @ApiResponse(
        responseCode = "400",
        description = "Bad Request.",
        content = [
            Content(
                schema = Schema(implementation = ValidationErrorDTO::class),
                examples = [
                    ExampleObject(
                        "{" +
                            "\"code\": \"$BAD_REQUEST_CODE\"," +
                            "\"message\": \"$BAD_REQUEST_MSG\"" +
                            "}"
                    ),
                ]
            )
        ]
    )
    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(error: ServerWebInputException): ResponseEntity<ErrorDTO> {
        try {
            return ResponseEntity(
                ErrorDTO(
                    code = BAD_REQUEST_CODE,
                    message = BAD_REQUEST_MSG
                ),
                HttpStatus.BAD_REQUEST
            )
        } catch (ex: java.lang.RuntimeException) {
            logger.error { ex }
            throw ex
        }
    }

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
    @ExceptionHandler(NotImplementedError::class)
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
