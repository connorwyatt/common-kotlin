package io.connorwyatt.common.server

import io.connorwyatt.common.eventstore.configuration.EventStoreConfiguration
import io.connorwyatt.common.eventstore.kodein.eventStoreDependenciesModule
import io.connorwyatt.common.eventstore.ktor.configureEventStore
import io.connorwyatt.common.http.httpDependenciesModule
import io.connorwyatt.common.http.validation.ValidationProblemResponse
import io.connorwyatt.common.mongodb.configuration.MongoDBConfiguration
import io.connorwyatt.common.mongodb.kodein.mongoDBDependenciesModule
import io.connorwyatt.common.mongodb.ktor.configureMongoDB
import io.connorwyatt.common.rabbitmq.configuration.RabbitMQConfiguration
import io.connorwyatt.common.rabbitmq.kodein.rabbitMQDependenciesModule
import io.connorwyatt.common.rabbitmq.ktor.configureRabbitMQ
import io.connorwyatt.common.time.timeDependenciesModule
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.ktor.di

class Server internal constructor(private val embeddedServer: ApplicationEngine) {
    fun start() {
        embeddedServer.start(wait = true)
    }

    class Builder internal constructor() {
        private var port: Int? = null
        private var diModules = listOf<DI.Module>()
        private var eventStoreConfiguration: EventStoreConfiguration? = null
        private var mongoDBConfiguration: MongoDBConfiguration? = null
        private var rabbitMQConfiguration: RabbitMQConfiguration? = null
        private var http: Boolean = false
        private var time: Boolean = false
        private var configureRequestValidation: (RequestValidationConfig.() -> Unit)? = null
        private var configureStatusPages: (StatusPagesConfig.() -> Unit)? = null
        private var configureRouting: (Routing.() -> Unit)? = null

        fun port(port: Int) {
            this.port = port
        }

        fun addDIModule(diModule: DI.Module) {
            diModules = diModules.plus(diModule)
        }

        fun addEventStore(eventStoreConfiguration: EventStoreConfiguration) {
            this.eventStoreConfiguration = eventStoreConfiguration
        }

        fun addMongoDB(mongoDBConfiguration: MongoDBConfiguration) {
            this.mongoDBConfiguration = mongoDBConfiguration
        }

        fun addRabbitMQ(rabbitMQConfiguration: RabbitMQConfiguration) {
            this.rabbitMQConfiguration = rabbitMQConfiguration
        }

        fun addHttp() {
            this.http = true
        }

        fun addTime() {
            this.time = true
        }

        fun configureRequestValidation(
            configureRequestValidation: RequestValidationConfig.() -> Unit
        ) {
            this.configureRequestValidation = configureRequestValidation
        }

        fun configureStatusPages(configureStatusPages: StatusPagesConfig.() -> Unit) {
            this.configureStatusPages = configureStatusPages
        }

        fun configureRouting(configureRouting: Routing.() -> Unit) {
            this.configureRouting = configureRouting
        }

        internal fun build(): Server {
            val port = port ?: throw Exception("Cannot build server without a port to listen on.")

            return Server(
                embeddedServer(Netty, port = port, host = "localhost") {
                    runBlocking {
                        di {
                            eventStoreConfiguration?.let {
                                import(eventStoreDependenciesModule(it))
                            }
                            mongoDBConfiguration?.let { import(mongoDBDependenciesModule(it)) }
                            rabbitMQConfiguration?.let { import(rabbitMQDependenciesModule(it)) }
                            if (http) {
                                import(httpDependenciesModule)
                            }
                            if (time) {
                                import(timeDependenciesModule)
                            }
                            importAll(diModules)
                        }
                        mongoDBConfiguration?.let { configureMongoDB() }
                        eventStoreConfiguration?.let { configureEventStore(it) }
                        rabbitMQConfiguration?.let { configureRabbitMQ(it) }
                        configureSerialization(this@embeddedServer)
                        configureRequestValidation(this@embeddedServer)
                        configureStatusPages(this@embeddedServer)
                        configureCallId(this@embeddedServer)
                        configureCallLogging(this@embeddedServer)
                        configureRouting(this@embeddedServer)
                    }
                }
            )
        }

        private fun configureSerialization(application: Application) {
            application.install(ContentNegotiation) { json() }
        }

        private fun configureRequestValidation(application: Application) {
            configureRequestValidation?.let { configure ->
                application.install(RequestValidation) { configure.invoke(this) }
            }
        }

        private fun configureStatusPages(application: Application) {
            application.install(StatusPages) {
                exception<RequestValidationException> { call, cause ->
                    call.response.headers.append(
                        HttpHeaders.ContentType,
                        ContentType.Application.ProblemJson.toString()
                    )
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ValidationProblemResponse(cause.reasons)
                    )
                }
                exception<Throwable> { call, _ ->
                    call.respondText(
                        "",
                        ContentType.Any,
                        status = HttpStatusCode.InternalServerError
                    )
                }
                configureStatusPages?.invoke(this)
            }
        }

        private fun configureCallId(application: Application) {
            application.install(CallId) {
                generate { UUID.randomUUID().toString() }
                replyToHeader(HttpHeaders.XRequestId)
            }
        }

        private fun configureCallLogging(application: Application) {
            application.install(CallLogging) {
                callIdMdc("request-id")
                disableDefaultColors()
                mdc("http-method") { call -> call.request.httpMethod.value }
                mdc("request-url") { call -> call.request.uri }
                mdc("status-code") { call -> call.response.status()?.value?.toString() }
            }
        }

        private fun configureRouting(application: Application) {
            configureRouting?.let { configure -> application.routing { configure.invoke(this) } }
        }
    }

    companion object {
        fun build(builder: Builder.() -> Unit): Server =
            Builder().apply { builder.invoke(this) }.build()
    }
}
