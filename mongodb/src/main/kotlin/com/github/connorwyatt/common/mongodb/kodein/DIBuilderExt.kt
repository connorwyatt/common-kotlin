package com.github.connorwyatt.common.mongodb.kodein

import com.github.connorwyatt.common.mongodb.MongoDBCollectionDefinition
import com.github.connorwyatt.common.mongodb.collectionName
import org.kodein.di.*

inline fun <reified T> DI.Builder.bindMongoDBCollection() {
    inBindSet<MongoDBCollectionDefinition> {
        add { singleton { MongoDBCollectionDefinition(collectionName<T>()) } }
    }
}
