dependencies {
    implementation(project(":configuration"))
    implementation(project(":eventstore"))
    implementation(project(":http"))
    implementation(project(":mongodb"))
    implementation(project(":rabbitmq"))

    implementation(libraries.kodein.di)
    implementation(libraries.kodein.di.framework.ktor.server)
    implementation(libraries.ktor.serialization.kotlinx.json)
    implementation(libraries.ktor.server.callId)
    implementation(libraries.ktor.server.callLogging)
    implementation(libraries.ktor.server.contentNegotiation)
    implementation(libraries.ktor.server.core)
    implementation(libraries.ktor.server.netty)
    implementation(libraries.ktor.server.requestValidation)
    implementation(libraries.ktor.server.statusPages)
}
