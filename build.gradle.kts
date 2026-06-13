plugins {
    kotlin("jvm") version libs.versions.kotlin.get()
    jacoco
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

dependencies {
    testImplementation(libs.selenium)

    testImplementation(libs.webdriver.manager)

    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotest.assertions.core)
}

tasks.test {
    useJUnitPlatform()

    systemProperty("browserName", System.getProperty("browserName", "chrome"))
    systemProperty("headless", System.getProperty("headless", "true"))

    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }
}
