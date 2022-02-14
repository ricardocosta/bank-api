package com.ricardocosta.api.bank.dto

data class ValidationErrorDTO(
    val code: String,
    val message: String,
    val validationErrorDetails: List<ValidationErrorDetailsDTO>
)

data class ValidationErrorDetailsDTO(
    val code: String,
    val field: String,
    val message: String,
)
