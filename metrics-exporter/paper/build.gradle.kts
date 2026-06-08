plugins {
    java
    id("vc.maximum.mc.release")
    id("vc.maximum.mc.shadow-jar")
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":metrics-exporter:bukkit"))

    val paperVersion: String by rootProject
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
}

mcRelease {
    pluginId.set("metrics-exporter")
}

mcPluginShadow {
    archiveFileName = "MaximumMetricsExporter-Paper.jar"
    relocation("io.prometheus", "vc.maximum.mc.metricsexporter.lib.prometheus")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}
