package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory
import tpo.lab3.config.TestConfig

class AuthPage(
    webDriver: WebDriver,
    private val baseUrl: String = TestConfig.baseUrl.trimEnd('/') + "/auth",
): BasePage(webDriver) {

    init {
        PageFactory.initElements(webDriver, this)
    }

    private val usernameInput = By.xpath("//input[@name='login_username']")
    private val passwordInput = By.xpath("//input[@name='login_password']")

    private val submitButton = By.xpath("//button[@type='submit']")

    private val errorBar = By.xpath("//article[@id='post-574']/div/div")

    fun open(): AuthPage {
        webDriver.navigate().to(baseUrl)
        return this
    }

    fun waitUntilOpened(): AuthPage {
        waitUntilUrlContains("/auth")
        waitUntilVisible(usernameInput)
        waitUntilVisible(passwordInput)
        waitUntilClickable(submitButton)
        return this
    }

    fun enterUsername(username: String): AuthPage {
        val el = waitUntilVisible(usernameInput)
        el.clear()
        el.sendKeys(username)
        return this
    }

    fun enterPassword(password: String): AuthPage {
        val el = waitUntilVisible(passwordInput)
        el.clear()
        el.sendKeys(password)
        return this
    }

    fun clickLogInButton() {
        val el = waitUntilClickable(submitButton)
        clickElement(el)
    }

    fun loginAs(username: String, password: String) =
        enterUsername(username)
            .enterPassword(password)
            .clickLogInButton()

    fun errorMessage(): String? = waitUntilVisible(errorBar).text
}