dependencies {
    implementation(projects.configuration)
    implementation(projects.eventstore)
    implementation(projects.http)
    implementation(projects.mongodb)
    implementation(projects.rabbitmq)
    implementation(projects.time)

    implementation(libraries.kodein.di)
    implementation(libraries.kodein.di.framework.ktor.server)
    implementation(libraries.ktor.serialization.kotlinx.json)
    implementation(libraries.ktor.server.callId)
    implementation(libraries.ktor.server.callLogging)
    implementation(libraries.ktor.server.cio)
    implementation(libraries.ktor.server.contentNegotiation)
    implementation(libraries.ktor.server.core)
    implementation(libraries.ktor.server.requestValidation)
    implementation(libraries.ktor.server.statusPages)
}
