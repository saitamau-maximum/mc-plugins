plugins {
    `java-library`
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":metrics-exporter:core"))

    val paperVersion: String by rootProject
    compileOnly("io.papermc.paper:paper-api:$paperVersion")
}
