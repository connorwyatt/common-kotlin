package io.connorwyatt.common.http

import io.ktor.client.statement.*

open class BaseHttpResult(private val response: HttpResponse) {
    val status = response.status
}
