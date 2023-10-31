plugins { id("org.jetbrains.kotlin.plugin.serialization") }

dependencies {
    implementation(libraries.kodein.di)
    implementation(libraries.kotlinx.serialization.json)

    testImplementation(testingLibraries.jUnit.jupiter)
    testImplementation(testingLibraries.strikt.core)

    testRuntimeOnly(testingLibraries.jUnit.jupiter.engine)
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
