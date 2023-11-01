package io.connorwyatt.common.mongodb.kodein

import io.connorwyatt.common.mongodb.MongoDBCollectionDefinition
import io.connorwyatt.common.mongodb.collectionName
import org.kodein.di.*

inline fun <reified T> DI.Builder.bindMongoDBCollection() {
    inBindSet<MongoDBCollectionDefinition> {
        add { singleton { MongoDBCollectionDefinition(collectionName<T>()) } }
    }
}
