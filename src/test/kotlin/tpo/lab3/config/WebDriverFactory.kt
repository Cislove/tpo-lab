package tpo.lab3.config

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import java.time.Duration

object WebDriverFactory {
    fun create(config: TestConfig): WebDriver {
        println("browser=${config.browserName}, headless=${config.headless}")
        val driver = when (config.browserName) {
            "chrome" -> createChromeDriver(config)
            "firefox" -> createFirefoxDriver(config)
            else -> throw IllegalArgumentException("Unsupported browser: ${config.browserName}")
        }

        driver.manage().timeouts().implicitlyWait(Duration.ZERO)

        return driver
    }

    private fun createChromeDriver(config: TestConfig): WebDriver {
        WebDriverManager.chromedriver().setup()

        val options = ChromeOptions()
        if (config.headless) {
            options.addArguments("--headless=new")
        }
        options.addArguments("--disable-dev-shm-usage")
        options.addArguments("--no-sandbox")
        options.addArguments("--window-size=1920,1080")

        return ChromeDriver(options)
    }

    private fun createFirefoxDriver(config: TestConfig): WebDriver {
        WebDriverManager.firefoxdriver().setup()

        val options = FirefoxOptions()
        if (config.headless) {
            options.addArguments("--headless")
        }
        options.addArguments("--width=1920")
        options.addArguments("--height=1080")

        return FirefoxDriver(options)
    }
}