dependencies {
    implementation(project(":http"))

    implementation(libraries.kodein.di)
    implementation(libraries.ktor.client.cio)
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
