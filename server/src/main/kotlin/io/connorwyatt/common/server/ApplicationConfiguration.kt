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

class ApplicationConfiguration(block: Builder.() -> Unit) {
    val di by lazy {
        DI {
            importAll(builder.builderDiModules)
            importAll(builder.diModules)
        }
    }

    private val builder: Builder = Builder().apply(block)

    fun applyTo(application: Application) {
        application.apply {
            di { extend(di) }
            builder.mongoDBConfiguration?.let { runBlocking { configureMongoDB() } }
            builder.eventStoreConfiguration?.let { configureEventStore(it) }
            builder.rabbitMQConfiguration?.let { configureRabbitMQ(it) }
            configureSerialization()
            builder.configureRequestValidation?.let { configureRequestValidation(it) }
            configureStatusPages(builder.configureStatusPages)
            configureCallId()
            configureCallLogging()
            builder.configureRouting?.let { configureRouting(it) }
        }
    }

    class Builder internal constructor() {
        internal var diModules = listOf<DI.Module>()
            private set

        internal var builderDiModules = listOf<DI.Module>()
            private set

        internal var eventStoreConfiguration: EventStoreConfiguration? = null
            private set

        internal var mongoDBConfiguration: MongoDBConfiguration? = null
            private set

        internal var rabbitMQConfiguration: RabbitMQConfiguration? = null
            private set

        internal var http: Boolean = false
            private set

        internal var time: Boolean = false
            private set

        internal var configureRequestValidation: (RequestValidationConfig.() -> Unit)? = null
            private set

        internal var configureStatusPages: (StatusPagesConfig.() -> Unit)? = null
            private set

        internal var configureRouting: (Routing.() -> Unit)? = null
            private set

        fun addDIModule(diModule: DI.Module) {
            diModules = diModules.plus(diModule)
        }

        fun addEventStore(eventStoreConfiguration: EventStoreConfiguration) {
            this.eventStoreConfiguration = eventStoreConfiguration
            builderDiModules =
                builderDiModules.plus(eventStoreDependenciesModule(eventStoreConfiguration))
        }

        fun addMongoDB(mongoDBConfiguration: MongoDBConfiguration) {
            this.mongoDBConfiguration = mongoDBConfiguration
            builderDiModules =
                builderDiModules.plus(mongoDBDependenciesModule(mongoDBConfiguration))
        }

        fun addRabbitMQ(rabbitMQConfiguration: RabbitMQConfiguration) {
            this.rabbitMQConfiguration = rabbitMQConfiguration
            builderDiModules =
                builderDiModules.plus(rabbitMQDependenciesModule(rabbitMQConfiguration))
        }

        fun addHttp() {
            this.http = true
            builderDiModules = builderDiModules.plus(httpDependenciesModule)
        }

        fun addTime() {
            this.time = true
            builderDiModules = builderDiModules.plus(timeDependenciesModule)
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
    }
}
