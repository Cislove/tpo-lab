package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory
import org.openqa.selenium.support.ui.ExpectedConditions
import tpo.lab3.config.TestConfig

open class UserProfilePage (
    webDriver: WebDriver,
    protected val baseUrl: String = TestConfig.baseUrl.trimEnd('/') + "/anketa/",
): BasePage(webDriver) {
    protected var id: String = "1"

    init {
        PageFactory.initElements(webDriver, this)
    }

    private val birthday = By.xpath("//strong[text()='Дата рождения']/following-sibling::div")
    private val country = By.xpath("//strong[text()='Страна']/following-sibling::div")
    private val region = By.xpath("//strong[text()='Область']/following-sibling::div")
    private val city = By.xpath("//strong[text()='Город']/following-sibling::div")
    private val age = By.xpath("//span[i[contains(@class, 'fa-birthday-cake')]]")
    private val height = By.xpath("//strong[text()='Рост']/following-sibling::div")
    private val genderIcon = By.xpath("//p[@class='profile-basic-details']/span/i[contains(@class, 'fa-')]")

    fun open(id: String): UserProfilePage {
        this.id = id
        webDriver.get("${baseUrl}${id}/")
        return this
    }

    open fun waitUntilOpened(id: String = this.id): UserProfilePage {
        this.id = id
        waitUntilUrlContains("${baseUrl}${this.id}")
        return this
    }

    fun readBirthday(): String = getFieldText(birthday)

    fun readCountry(): String = getFieldText(country)

    fun readCity(): String = getFieldText(city)

    fun readAge(): String = getFieldText(age)

    fun readHeight(): String = getFieldText(height)

    fun readGender(): String {
        return try {
            val el = wait.until(ExpectedConditions.presenceOfElementLocated(genderIcon))
            val className = el.getAttribute("class") ?: ""
            when {
                className.contains("fa-female") -> "Женщина"
                className.contains("fa-male") -> "Мужчина"
                else -> "Неизвестно"
            }
        } catch (e: Exception) {
            "Неизвестно"
        }
    }

    private fun getFieldText(locator: By): String {
        return try {
            val el = wait.until(ExpectedConditions.presenceOfElementLocated(locator))
            val text = el.getAttribute("textContent")?.trim() ?: ""
            text.replace(Regex("^[^a-zA-Zа-яА-Я0-9]+"), "").trim()
        } catch (e: Exception) {
            ""
        }
    }
}