plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(libraries.kodein.di)
    implementation(libraries.kodein.di.framework.ktor.server)
    implementation(libraries.kotlinx.coroutines.core)
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.server.core)
    implementation(libraries.rabbitMQ.client)
}
