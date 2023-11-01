package io.connorwyatt.common.mongodb.kodein

import com.mongodb.kotlin.client.coroutine.MongoClient
import io.connorwyatt.common.mongodb.MongoDBCollectionDefinition
import io.connorwyatt.common.mongodb.MongoDBInitializer
import io.connorwyatt.common.mongodb.configuration.MongoDBConfiguration
import org.kodein.di.*

fun mongoDBDependenciesModule(mongoDBConfiguration: MongoDBConfiguration): DI.Module =
    DI.Module(name = ::mongoDBDependenciesModule.name) {
        mongoDBConfiguration.connectionString?.let { connectionString ->
            bindSingleton { MongoClient.create(connectionString) }
            bindSingleton {
                MongoDBInitializer(mongoDBConfiguration.databaseName, instance(), instance())
            }
        }

        bindSet<MongoDBCollectionDefinition>()
    }
