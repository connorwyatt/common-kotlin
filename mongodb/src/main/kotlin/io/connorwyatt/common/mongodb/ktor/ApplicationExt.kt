package io.connorwyatt.common.mongodb.ktor

import io.connorwyatt.common.mongodb.MongoDBInitializer
import io.ktor.server.application.*
import org.kodein.di.instanceOrNull
import org.kodein.di.ktor.closestDI

suspend fun Application.configureMongoDB() {
    val mongoDBInitializer by closestDI().instanceOrNull<MongoDBInitializer>()

    mongoDBInitializer?.initialize()
}
