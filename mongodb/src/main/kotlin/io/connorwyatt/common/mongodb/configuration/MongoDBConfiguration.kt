package io.connorwyatt.common.mongodb.configuration

data class MongoDBConfiguration(
    val connectionString: String?,
    val databaseName: String,
)
