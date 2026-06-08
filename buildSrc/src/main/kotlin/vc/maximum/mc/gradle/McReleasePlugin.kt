package vc.maximum.mc.gradle

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

class McReleasePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("mcRelease", McReleaseExtension::class.java)

        project.afterEvaluate {
            if (!extension.pluginId.isPresent) {
                throw GradleException("mcRelease.pluginId must be set in ${project.path}")
            }
            if (!extension.runtime.isPresent || extension.runtime.get().isBlank()) {
                extension.runtime.set(project.name)
            }
        }
    }
}
