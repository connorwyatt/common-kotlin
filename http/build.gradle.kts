plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(libraries.kodein.di)
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.client.cio)
    implementation(libraries.ktor.client.core)
}
