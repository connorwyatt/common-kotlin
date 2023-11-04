package io.connorwyatt.common.server

import io.connorwyatt.common.eventstore.configuration.EventStoreConfiguration
import io.connorwyatt.common.eventstore.kodein.eventStoreDependenciesModule
import io.connorwyatt.common.eventstore.ktor.configureEventStore
import io.connorwyatt.common.http.httpDependenciesModule
import io.connorwyatt.common.mongodb.configuration.MongoDBConfiguration
import io.connorwyatt.common.mongodb.kodein.mongoDBDependenciesModule
import io.connorwyatt.common.mongodb.ktor.configureMongoDB
import io.connorwyatt.common.rabbitmq.configuration.RabbitMQConfiguration
import io.connorwyatt.common.rabbitmq.kodein.rabbitMQDependenciesModule
import io.connorwyatt.common.rabbitmq.ktor.configureRabbitMQ
import io.connorwyatt.common.time.timeDependenciesModule
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.kodein.di.DI
import org.kodein.di.ktor.di

class ApplicationConfiguration {
    private var diModules = listOf<DI.Module>()
    private var eventStoreConfiguration: EventStoreConfiguration? = null
    private var mongoDBConfiguration: MongoDBConfiguration? = null
    private var rabbitMQConfiguration: RabbitMQConfiguration? = null
    private var http: Boolean = false
    private var time: Boolean = false
    private var configureRequestValidation: (RequestValidationConfig.() -> Unit)? = null
    private var configureStatusPages: (StatusPagesConfig.() -> Unit)? = null
    private var configureRouting: (Routing.() -> Unit)? = null

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

    fun configureRequestValidation(configureRequestValidation: RequestValidationConfig.() -> Unit) {
        this.configureRequestValidation = configureRequestValidation
    }

    fun configureStatusPages(configureStatusPages: StatusPagesConfig.() -> Unit) {
        this.configureStatusPages = configureStatusPages
    }

    fun configureRouting(configureRouting: Routing.() -> Unit) {
        this.configureRouting = configureRouting
    }

    fun applyTo(application: Application) {
        application.apply {
            di {
                eventStoreConfiguration?.let { import(eventStoreDependenciesModule(it)) }
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
            mongoDBConfiguration?.let { runBlocking { configureMongoDB() } }
            eventStoreConfiguration?.let { configureEventStore(it) }
            rabbitMQConfiguration?.let { configureRabbitMQ(it) }
            configureSerialization()
            configureRequestValidation?.let { configureRequestValidation(it) }
            configureStatusPages(configureStatusPages)
            configureCallId()
            configureCallLogging()
            configureRouting?.let { configureRouting(it) }
        }
    }
}
