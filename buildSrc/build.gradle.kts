plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("mcRelease") {
            id = "vc.maximum.mc.release"
            implementationClass = "vc.maximum.mc.gradle.McReleasePlugin"
        }
        create("mcPluginShadow") {
            id = "vc.maximum.mc.shadow-jar"
            implementationClass = "vc.maximum.mc.gradle.McPluginShadowPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.2.0")
}
