plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(project(":time"))

    implementation(libraries.eventStore.client)
    implementation(libraries.kotlin.reflect)
    implementation(libraries.kotlinx.coroutines.core)
    implementation(libraries.kotlinx.serialization.json)

    testImplementation(testingLibraries.jUnit.jupiter)
    testImplementation(testingLibraries.strikt.core)

    testRuntimeOnly(testingLibraries.jUnit.jupiter.engine)
}
