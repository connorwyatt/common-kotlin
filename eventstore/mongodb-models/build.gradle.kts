dependencies {
    implementation(project(":eventstore"))
    implementation(project(":mongodb"))

    implementation(libraries.mongoDB.driver)
}
