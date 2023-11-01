dependencies {
    implementation(libraries.hoplite.core)
    implementation(libraries.hoplite.json)

    testImplementation(testingLibraries.jUnit.jupiter)
    testImplementation(testingLibraries.strikt.core)

    testRuntimeOnly(testingLibraries.jUnit.jupiter.engine)
}
