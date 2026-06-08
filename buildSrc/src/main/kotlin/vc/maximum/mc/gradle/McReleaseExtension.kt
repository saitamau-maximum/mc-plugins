package vc.maximum.mc.gradle

import org.gradle.api.provider.Property

abstract class McReleaseExtension {
    abstract val pluginId: Property<String>

    /** Runtime adapter name (e.g. bukkit, paper, folia). Defaults to the subproject directory name. */
    abstract val runtime: Property<String>
}
