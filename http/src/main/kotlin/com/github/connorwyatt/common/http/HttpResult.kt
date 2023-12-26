package com.github.connorwyatt.common.http

import io.ktor.client.call.*
import io.ktor.client.statement.*

class HttpResult<T>(response: HttpResponse, private val bodyFn: suspend () -> T) :
    BaseHttpResult(response) {
    suspend fun body(): T = bodyFn()

    companion object {
        suspend inline fun <reified T> fromResponse(response: HttpResponse): HttpResult<T> =
            HttpResult(response) { response.body<T>() }
    }
}
