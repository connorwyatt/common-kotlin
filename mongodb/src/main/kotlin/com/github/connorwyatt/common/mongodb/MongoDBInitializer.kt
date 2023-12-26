package com.github.connorwyatt.common.mongodb

import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

class MongoDBInitializer(
    private val databaseName: String,
    private val mongoClient: MongoClient,
    private val collectionDefinitions: Set<MongoDBCollectionDefinition>
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default)

    suspend fun initialize() {
        mongoClient.getDatabase(databaseName).apply {
            collectionDefinitions
                .map { coroutineScope.launch { createCollection(it.collectionName) } }
                .joinAll()
        }
    }
}
