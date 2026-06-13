package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory
import tpo.lab3.config.TestConfig

class HomePage(
    webDriver: WebDriver,
    private val baseUrl: String = TestConfig.baseUrl,
): BasePage(webDriver) {

    init {
        PageFactory.initElements(webDriver, this)
    }

    private val logInInput = By.xpath("//a[contains(text(),'Вход')]")

    fun waitUntilOpened(): HomePage {
        waitUntilUrlContains("znakomstva")
        return this
    }

    fun open(): HomePage {
        webDriver.navigate().to(baseUrl)
        return this
    }

    fun clickLogInButton(): AuthPage {
        clickElement(waitUntilClickable(logInInput))
        return AuthPage(webDriver)
     }
}