dependencies {
    testImplementation(testingLibraries.jUnit.jupiter)
    testImplementation(testingLibraries.strikt.core)

    testRuntimeOnly(testingLibraries.jUnit.jupiter.engine)
}
