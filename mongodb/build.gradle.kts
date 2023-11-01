plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    api(libraries.mongoDB.driver)

    implementation(libraries.kodein.di)
    implementation(libraries.kodein.di.framework.ktor.server)
    implementation(libraries.kotlin.reflect)
    implementation(libraries.kotlinx.coroutines.core)
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.server.core)
}
