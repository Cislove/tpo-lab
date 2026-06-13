package tpo.lab3.tests

import org.openqa.selenium.WebDriver
import tpo.lab3.config.TestConfig
import tpo.lab3.config.WebDriverFactory

interface BaseTest {
    var driver: WebDriver

    fun createDriver() {
        driver = WebDriverFactory.create(TestConfig)
    }

    fun quitDriver() {
        runCatching { driver.quit() }
    }
}
