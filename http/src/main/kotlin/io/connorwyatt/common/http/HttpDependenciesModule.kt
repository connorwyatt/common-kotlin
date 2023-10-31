package io.connorwyatt.common.http

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import org.kodein.di.*

const val DEFAULT_JSON_HTTP_CLIENT_TAG = "DEFAULT_JSON_HTTP_CLIENT"

val httpDependenciesModule by
    DI.Module { bindProvider(tag = DEFAULT_JSON_HTTP_CLIENT_TAG) { HttpClient(CIO) } }
