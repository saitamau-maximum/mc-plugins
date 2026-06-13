plugins {
    id("com.diffplug.spotless") version "8.6.0"
}

repositories {
    mavenCentral()
}

spotless {
    java {
        target("login-notify/**/src/*/java/**/*.java")
        googleJavaFormat("1.25.2")
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("*.gradle.kts", "login-notify/**/*.gradle.kts")
        ktlint()
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
