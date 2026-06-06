plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "mc-plugins"

include("login-notify:core", "login-notify:bukkit")
