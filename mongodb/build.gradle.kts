plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(libraries.kodein.di)
    implementation(libraries.kodein.di.framework.ktor.server)
    implementation(libraries.kotlin.reflect)
    implementation(libraries.kotlinx.coroutines.core)
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.server.core)
    implementation(libraries.mongoDB.driver)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.path.trimStart(':').replace(':', '-')
            version = project.version.toString()

            from(components["java"])
        }
    }
}
