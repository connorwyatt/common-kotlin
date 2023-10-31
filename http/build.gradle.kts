plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(libraries.kotlinx.serialization.json)
    implementation(libraries.ktor.client.core)
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
