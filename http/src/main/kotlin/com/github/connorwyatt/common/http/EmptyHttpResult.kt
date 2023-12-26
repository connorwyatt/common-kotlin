package com.github.connorwyatt.common.http

import io.ktor.client.statement.*

class EmptyHttpResult(response: HttpResponse) : BaseHttpResult(response) {
    companion object {
        fun fromResponse(response: HttpResponse): EmptyHttpResult = EmptyHttpResult(response)
    }
}
