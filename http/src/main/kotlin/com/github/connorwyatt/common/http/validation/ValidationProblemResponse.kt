package com.github.connorwyatt.common.http.validation

import kotlinx.serialization.*

@Serializable
data class ValidationProblemResponse(val validationErrors: List<String>) : ProblemResponse {
    override val type: String = "validationError"
    override val title: String = "A validation error occurred."
    override val status: Int = 400
    override val detail: String =
        "A validation error occurred. Check the validation errors for more details."
}
