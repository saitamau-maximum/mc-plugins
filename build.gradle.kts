subprojects {
    group = "vc.maximum.mc"
    version = "1.0.0"

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
