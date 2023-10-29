dependencies {
    implementation(project(":eventstore"))
    implementation(project(":time"))

    implementation(libraries.eventStore.client)
    implementation(libraries.kodein.di)
}
