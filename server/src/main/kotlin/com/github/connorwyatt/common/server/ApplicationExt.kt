package com.github.connorwyatt.common.server

import com.github.connorwyatt.common.http.validation.ValidationProblemResponse
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

internal fun Application.configureSerialization() {
    install(ContentNegotiation) { json() }
}

internal fun Application.configureRequestValidation(configure: RequestValidationConfig.() -> Unit) {
    install(RequestValidation) { configure.invoke(this) }
}

internal fun Application.configureStatusPages(configure: (StatusPagesConfig.() -> Unit)? = null) {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            call.response.headers.append(
                HttpHeaders.ContentType,
                ContentType.Application.ProblemJson.toString()
            )
            call.respond(HttpStatusCode.BadRequest, ValidationProblemResponse(cause.reasons))
        }
        exception<Throwable> { call, _ ->
            call.respondText("", ContentType.Any, status = HttpStatusCode.InternalServerError)
        }
        configure?.invoke(this)
    }
}

internal fun Application.configureCallId() {
    install(CallId) {
        generate { UUID.randomUUID().toString() }
        replyToHeader(HttpHeaders.XRequestId)
    }
}

internal fun Application.configureCallLogging() {
    install(CallLogging) {
        callIdMdc("request-id")
        disableDefaultColors()
        mdc("http-method") { call -> call.request.httpMethod.value }
        mdc("request-url") { call -> call.request.uri }
        mdc("status-code") { call -> call.response.status()?.value?.toString() }
    }
}

internal fun Application.configureRouting(configure: Routing.() -> Unit) {
    routing { configure.invoke(this) }
}
