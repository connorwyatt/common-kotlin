plugins {
    id("com.diffplug.spotless")
    id("org.jetbrains.kotlin.jvm") apply false
    id("org.jetbrains.kotlin.plugin.serialization") apply false
    id("pl.allegro.tech.build.axion-release")
}

scmVersion {
    useHighestVersion.set(true)
    tag {
        prefix.set("")
        initialVersion { _, _ -> "1.0.0" }
    }
    versionCreator { versionFromTag, position ->
        if (position.branch == "main")
            versionFromTag
        else
            "${position.branch}-${position.shortRevision}"
    }
}

tasks {
    create("installLocalGitHook") {
        delete {
            delete(File(rootDir, ".git/hooks/pre-commit"))
        }
        copy {
            from(File(rootDir, "scripts/pre-commit"))
            into(File(rootDir, ".git/hooks"))
            fileMode = 0b111101101
        }
    }

    build {
        dependsOn("installLocalGitHook")
    }
}

allprojects {
    apply(plugin = "pl.allegro.tech.build.axion-release")

    scmVersion {
        useHighestVersion.set(true)
        tag {
            prefix.set("")
            initialVersion { _, _ -> "1.0.0" }
        }
        versionCreator { versionFromTag, position ->
            if (position.branch == "main")
                versionFromTag
            else
                "${position.branch}-${position.shortRevision}"
        }
        localOnly = true
    }

    project.group = "com.github.connorwyatt.common"
    project.version = scmVersion.version
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.jvm")

    spotless {
        kotlin {
            target("**/*.kt", "**/*.kts")
            ktfmt(project.properties["ktfmtVersion"] as String).kotlinlangStyle()
        }
    }

    tasks.withType(Test::class) {
        useJUnitPlatform()
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.path.trimStart(':').replace(':', '-')
                version = project.version.toString()

                from(components["java"])
            }
        }
    }
}
