package com.github.connorwyatt.common.server

import com.github.connorwyatt.common.eventstore.configuration.EventStoreConfiguration
import com.github.connorwyatt.common.eventstore.kodein.eventStoreDependenciesModule
import com.github.connorwyatt.common.eventstore.ktor.configureEventStore
import com.github.connorwyatt.common.http.httpDependenciesModule
import com.github.connorwyatt.common.mongodb.configuration.MongoDBConfiguration
import com.github.connorwyatt.common.mongodb.kodein.mongoDBDependenciesModule
import com.github.connorwyatt.common.mongodb.ktor.configureMongoDB
import com.github.connorwyatt.common.rabbitmq.configuration.RabbitMQConfiguration
import com.github.connorwyatt.common.rabbitmq.kodein.rabbitMQDependenciesModule
import com.github.connorwyatt.common.rabbitmq.ktor.configureRabbitMQ
import com.github.connorwyatt.common.time.timeDependenciesModule
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import org.kodein.di.*
import org.kodein.di.ktor.*

class ApplicationConfiguration
internal constructor(val di: DI, private val configureApplication: Application.() -> Unit) {
    fun applyTo(application: Application) {
        configureApplication.invoke(application)
    }

    class Builder {
        private var diModules = listOf<DI.Module>()
        private var builderDIModules = listOf<DI.Module>()
        private var allowDIOverrides = false
        private var eventStoreConfiguration: EventStoreConfiguration? = null
        private var mongoDBConfiguration: MongoDBConfiguration? = null
        private var rabbitMQConfiguration: RabbitMQConfiguration? = null
        private var http: Boolean = false
        private var time: Boolean = false
        private var configureRequestValidation: (RequestValidationConfig.() -> Unit)? = null
        private var configureStatusPages: (StatusPagesConfig.() -> Unit)? = null
        private var configureRouting: (Routing.() -> Unit)? = null

        fun addDIModule(diModule: DI.Module): Builder {
            this.diModules = this.diModules.plus(diModules)
            return this
        }

        fun addDIModules(diModules: List<DI.Module>): Builder {
            this.diModules = this.diModules.plus(diModules)
            return this
        }

        fun allowDIOverrides(allowDIOverrides: Boolean): Builder {
            this.allowDIOverrides = allowDIOverrides
            return this
        }

        fun addEventStore(eventStoreConfiguration: EventStoreConfiguration): Builder {
            this.eventStoreConfiguration = eventStoreConfiguration
            builderDIModules =
                builderDIModules.plus(eventStoreDependenciesModule(eventStoreConfiguration))
            return this
        }

        fun addMongoDB(mongoDBConfiguration: MongoDBConfiguration): Builder {
            this.mongoDBConfiguration = mongoDBConfiguration
            builderDIModules =
                builderDIModules.plus(mongoDBDependenciesModule(mongoDBConfiguration))
            return this
        }

        fun addRabbitMQ(rabbitMQConfiguration: RabbitMQConfiguration): Builder {
            this.rabbitMQConfiguration = rabbitMQConfiguration
            builderDIModules =
                builderDIModules.plus(rabbitMQDependenciesModule(rabbitMQConfiguration))
            return this
        }

        fun addHttp(): Builder {
            this.http = true
            builderDIModules = builderDIModules.plus(httpDependenciesModule)
            return this
        }

        fun addTime(): Builder {
            this.time = true
            builderDIModules = builderDIModules.plus(timeDependenciesModule)
            return this
        }

        fun configureRequestValidation(
            configureRequestValidation: RequestValidationConfig.() -> Unit
        ): Builder {
            this.configureRequestValidation = configureRequestValidation
            return this
        }

        fun configureStatusPages(configureStatusPages: StatusPagesConfig.() -> Unit): Builder {
            this.configureStatusPages = configureStatusPages
            return this
        }

        fun configureRouting(configureRouting: Routing.() -> Unit): Builder {
            this.configureRouting = configureRouting
            return this
        }

        fun build(): ApplicationConfiguration {
            val di = DI {
                importAll(builderDIModules, allowOverride = allowDIOverrides)
                importAll(diModules, allowOverride = allowDIOverrides)
            }

            return ApplicationConfiguration(di) {
                di { extend(di, allowOverride = allowDIOverrides) }
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
}
