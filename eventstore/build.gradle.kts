plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(projects.time)

    implementation(libraries.eventStore.client)
    implementation(libraries.kodein.di)
    implementation(libraries.kodein.di.framework.ktor.server)
    implementation(libraries.kotlin.reflect)
    implementation(libraries.kotlinx.coroutines.core)
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.server.core)

    testImplementation(testingLibraries.jUnit.jupiter)
    testImplementation(testingLibraries.jUnit.jupiter.params)
    testImplementation(testingLibraries.strikt.core)

    testRuntimeOnly(testingLibraries.jUnit.jupiter.engine)
}
