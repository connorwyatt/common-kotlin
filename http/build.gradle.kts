plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    api(libraries.ktor.client.core)

    implementation(libraries.kodein.di)
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.client.cio)
}
