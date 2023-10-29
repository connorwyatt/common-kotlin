rootProject.name = "common"
include(":eventstore")
include(":eventstore:kodein")
include(":time")
include(":time:kodein")

pluginManagement {
    val kotlinVersion: String by settings
    val spotlessVersion: String by settings

    repositories {
        gradlePluginPortal()
    }

    plugins {
        id("com.diffplug.spotless") version spotlessVersion
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }

    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    versionCatalogs {
        val eventStoreClientVersion: String by settings
        val jUnitVersion: String by settings
        val kodeinVersion: String by settings
        val kotlinVersion: String by settings
        val kotlinxCoroutinesVersion: String by settings
        val kotlinxSerializationVersion: String by settings
        val striktVersion: String by settings

        create("libraries") {
            library("eventStore-client", "com.eventstore", "db-client-java").version(eventStoreClientVersion)
            library("kodein-di", "org.kodein.di", "kodein-di").version(kodeinVersion)
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version(
                kotlinVersion
            )
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version(
                kotlinxCoroutinesVersion
            )
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version(
                kotlinxSerializationVersion
            )
        }

        create("testingLibraries") {
            library("jUnit-jupiter", "org.junit.jupiter", "junit-jupiter").version(jUnitVersion)
            library("jUnit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").version(jUnitVersion)
            library("strikt.core", "io.strikt", "strikt-core").version(striktVersion)
        }
    }
}
