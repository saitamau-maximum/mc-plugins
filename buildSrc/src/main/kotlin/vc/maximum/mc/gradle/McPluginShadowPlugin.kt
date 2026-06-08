package vc.maximum.mc.gradle

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.named

class McPluginShadowPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("mcPluginShadow", McPluginShadowExtension::class.java)
        project.pluginManager.apply("com.gradleup.shadow")

        project.plugins.withId("java") {
            project.afterEvaluate {
                val distributionJarName =
                    extension.archiveFileName?.takeIf { it.isNotBlank() }
                        ?: throw GradleException("mcPluginShadow.archiveFileName must be set in ${project.path}")

                project.tasks.named("jar", Jar::class.java).configure { enabled = false }

                project.tasks.named("shadowJar", ShadowJar::class.java).configure {
                    archiveClassifier.set("")
                    archiveFileName.set(distributionJarName)
                    extension.relocations().forEach { (fromPackage, toPackage) ->
                        relocate(fromPackage, toPackage)
                    }
                    mergeServiceFiles()
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                }

                project.tasks.named("assemble").configure { dependsOn("shadowJar") }
            }
        }
    }
}
