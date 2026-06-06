plugins {
    java
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":login-notify:core"))

    val paperVersion: String by rootProject
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
}

tasks.processResources {
    filesMatching("plugin.yml") {
        expand("version" to project.version)
    }
}

tasks.jar {
    archiveFileName.set("MaximumLoginNotify.jar")
    dependsOn(":login-notify:core:classes")
    from(
        project(":login-notify:core")
            .sourceSets.main
            .get()
            .output,
    )
}
