rootProject.name = "common"

include(":configuration")
include(":eventstore")
include(":eventstore:kodein")
include(":eventstore:kodein:ktor")
include(":optional")
include(":time")
include(":time:kodein")

pluginManagement {
    val axionReleaseVersion: String by settings
    val kotlinVersion: String by settings
    val spotlessVersion: String by settings

    repositories {
        gradlePluginPortal()
    }

    plugins {
        id("com.diffplug.spotless") version spotlessVersion
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.serialization") version kotlinVersion
        id("pl.allegro.tech.build.axion-release") version axionReleaseVersion
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
        val hopliteVersion: String by settings
        val jUnitVersion: String by settings
        val kodeinVersion: String by settings
        val kotlinVersion: String by settings
        val kotlinxCoroutinesVersion: String by settings
        val kotlinxSerializationVersion: String by settings
        val ktorVersion: String by settings
        val striktVersion: String by settings

        create("libraries") {
            library("eventStore-client", "com.eventstore", "db-client-java").version(eventStoreClientVersion)
            library("hoplite-core","com.sksamuel.hoplite","hoplite-core").version(hopliteVersion)
            library("hoplite-json","com.sksamuel.hoplite","hoplite-json").version(hopliteVersion)
            library("kodein-di", "org.kodein.di", "kodein-di").version(kodeinVersion)
            library("kodein-di-framework-ktor-server", "org.kodein.di", "kodein-di-framework-ktor-server-jvm").version(kodeinVersion)
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version(
                kotlinVersion
            )
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version(
                kotlinxCoroutinesVersion
            )
            library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version(
                kotlinxSerializationVersion
            )
            library("ktor-server-core", "io.ktor", "ktor-server-core").version(ktorVersion)
        }

        create("testingLibraries") {
            library("jUnit-jupiter", "org.junit.jupiter", "junit-jupiter").version(jUnitVersion)
            library("jUnit-jupiter-engine", "org.junit.jupiter", "junit-jupiter-engine").version(jUnitVersion)
            library("jUnit-jupiter-params", "org.junit.jupiter", "junit-jupiter-params").version(jUnitVersion)
            library("strikt.core", "io.strikt", "strikt-core").version(striktVersion)
        }
    }
}
