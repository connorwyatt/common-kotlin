package io.connorwyatt.common.http.validation

interface ProblemResponse {
    val type: String
    val title: String
    val status: Int
    val detail: String
}
