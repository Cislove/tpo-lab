rootProject.name = "selenium-kotest"

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.version.toml"))
        }
    }
    repositories {
        mavenCentral()
    }
}