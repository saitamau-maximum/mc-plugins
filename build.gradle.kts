import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.register
import vc.maximum.mc.gradle.McReleaseExtension

plugins {
    id("com.diffplug.spotless") version "7.0.2"
}

repositories {
    mavenCentral()
}

spotless {
    java {
        target("login-notify/**/src/*/java/**/*.java", "metrics-exporter/**/src/*/java/**/*.java")
        googleJavaFormat("1.25.2")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target(
            "*.gradle.kts",
            "buildSrc/**/*.gradle.kts",
            "login-notify/**/*.gradle.kts",
            "metrics-exporter/**/*.gradle.kts",
        )
        ktlint()
    }
}

fun Project.releaseSubprojects(
    pluginId: String,
    runtimeFilter: String? = null,
): List<Project> {
    val candidates =
        subprojects.filter { sub ->
            val release = sub.extensions.findByType(McReleaseExtension::class.java) ?: return@filter false
            release.pluginId.orNull == pluginId &&
                (runtimeFilter == null || release.runtime.orNull == runtimeFilter)
        }
    require(candidates.isNotEmpty()) {
        buildString {
            append("No release subprojects for plugin '$pluginId'")
            if (runtimeFilter != null) {
                append(" (runtime=$runtimeFilter)")
            }
            append(". Declared: ")
            append(
                subprojects
                    .mapNotNull { sub ->
                        sub.extensions.findByType(McReleaseExtension::class.java)?.let { release ->
                            "${release.pluginId.orNull}/${release.runtime.orNull}@${sub.path}"
                        }
                    }.joinToString(", "),
            )
        }
    }
    return candidates
}

gradle.projectsEvaluated {
    tasks.register("release") {
        group = "release"
        description = "Build release JAR(s) for -PreleasePlugin=<plugin-dir> (optional -PreleaseRuntime=...)"

        val pluginId = gradle.startParameter.projectProperties["releasePlugin"]
        if (pluginId.isNullOrBlank()) {
            doFirst {
                throw GradleException("Pass -PreleasePlugin=<plugin-dir> (e.g. login-notify)")
            }
            return@register
        }

        val runtimeFilter = gradle.startParameter.projectProperties["releaseRuntime"]?.takeIf { it.isNotBlank() }
        releaseSubprojects(pluginId, runtimeFilter).forEach { sub ->
            dependsOn(sub.tasks.named("shadowJar"))
        }
    }

    tasks.register<Copy>("stageReleaseArtifacts") {
        group = "release"
        description = "Build and copy release JAR(s) to build/release/<plugin-dir>/"

        val pluginId = gradle.startParameter.projectProperties["releasePlugin"]
        if (pluginId.isNullOrBlank()) {
            doFirst {
                throw GradleException("Pass -PreleasePlugin=<plugin-dir> (e.g. login-notify)")
            }
            return@register
        }

        dependsOn("release")
        val runtimeFilter = gradle.startParameter.projectProperties["releaseRuntime"]?.takeIf { it.isNotBlank() }
        into(layout.buildDirectory.dir("release/$pluginId"))
        releaseSubprojects(pluginId, runtimeFilter).forEach { sub ->
            from(sub.tasks.named("shadowJar", Jar::class.java).flatMap { it.archiveFile })
        }
    }

    tasks.register("printReleaseArtifacts") {
        group = "release"
        description = "Print staged release JAR paths (one per line, relative to repo root)"
        dependsOn("stageReleaseArtifacts")

        val pluginId = gradle.startParameter.projectProperties["releasePlugin"] ?: return@register
        doLast {
            fileTree(layout.buildDirectory.dir("release/$pluginId")).files.sorted().forEach { file ->
                println(file.relativeTo(rootDir))
            }
        }
    }
}

subprojects {
    group = "vc.maximum.mc"
    version = "0.0.1"

    repositories {
        mavenCentral()
    }

    plugins.withId("java") {
        the<JavaPluginExtension>().toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(21)
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }

    plugins.withId("java-library") {
        the<JavaPluginExtension>().toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }

        tasks.withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(21)
        }

        tasks.withType<Test> {
            useJUnitPlatform()
        }
    }
}
