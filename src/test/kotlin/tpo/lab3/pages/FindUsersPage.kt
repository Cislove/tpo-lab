package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.Select
import tpo.lab3.config.TestConfig

class FindUsersPage(
    webDriver: WebDriver,
    private val baseUrl: String = TestConfig.baseUrl.trimEnd('/') + "/ankety/"
): BasePage(webDriver) {

    private val sexSelect = By.xpath("//select[@name='sex']")
    private val countrySelect = By.xpath("//select[@name='country']")
    private val regionSelect = By.xpath("//select[@name='region']")
    private val citySelect = By.xpath("//select[@name='city']")
    private val ageMinInput = By.xpath("//input[@name='age_min']")
    private val ageMaxInput = By.xpath("//input[@name='age_max']")
    private val heightMinInput = By.xpath("//input[@name='height_min']")
    private val heightMaxInput = By.xpath("//input[@name='height_max']")
    private val orderBy = By.xpath("//select[@name='orderby']")
    private val searchButton = By.xpath("//button[@id='search-button']")

    private val usersCountField = By.xpath("//h2[@class='count-users']")

    fun open(): FindUsersPage {
        webDriver.navigate().to(baseUrl)
        return this
    }

    fun waitUntilOpened(): FindUsersPage {
        waitUntilUrlContains("/ankety")
        waitUntilVisible(searchButton)
        return this
    }

    fun selectSex(value: String): FindUsersPage {
        Select(waitUntilVisible(sexSelect)).selectByValue(value)
        Thread.sleep(2000)
        return this
    }

    fun selectCountry(value: String): FindUsersPage {
        Select(waitUntilVisible(countrySelect)).selectByValue(value)
        Thread.sleep(2000)
        return this
    }

    fun selectRegion(value: String): FindUsersPage {
        Select(waitUntilVisible(regionSelect)).selectByValue(value)
        Thread.sleep(2000)
        return this
    }

    fun selectCity(value: String): FindUsersPage {
        Select(waitUntilVisible(citySelect)).selectByValue(value)
        Thread.sleep(2000)
        return this
    }

    fun selectOrderBy(value: String): FindUsersPage {
        Select(waitUntilVisible(orderBy)).selectByValue(value)
        Thread.sleep(2000)
        return this
    }

    fun enterAgeMin(age: String): FindUsersPage {
        val el = waitUntilVisible(ageMinInput)
        el.clear()
        el.sendKeys(age)
        return this
    }

    fun enterAgeMax(age: String): FindUsersPage {
        val el = waitUntilVisible(ageMaxInput)
        el.clear()
        el.sendKeys(age)
        return this
    }

    fun enterHeightMin(height: String): FindUsersPage {
        val el = waitUntilVisible(heightMinInput)
        el.clear()
        el.sendKeys(height)
        return this
    }

    fun enterHeightMax(height: String): FindUsersPage {
        val el = waitUntilVisible(heightMaxInput)
        el.clear()
        el.sendKeys(height)
        return this
    }

    fun getUsersCount(): Int {
        return webDriver.findElement(usersCountField).text.split(" ")[1].toInt()
    }

    fun clickSearchButton() {
        clickElement(waitUntilClickable(searchButton))
        Thread.sleep(2000)
    }

    fun isUserCardPresent(userId: String): Boolean {
        val locator = By.xpath("//div[contains(@class, 'profile-card')][.//span[@data-user-id='$userId']]")
        waitUntilVisible(locator)
        return try {
            webDriver.findElements(locator).isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    fun getDisplayedUserIds(): List<String> {
        val locator = By.xpath("//span[@data-user-id]")
        return try {
            webDriver.findElements(locator)
                .mapNotNull { it.getAttribute("data-user-id") }
                .distinct()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clickUserProfile(userId: String) {
        val locator = By.xpath("//div[contains(@class, 'profile-card')][.//span[@data-user-id='$userId']]//a[contains(@class, 'view-profile-btn')]")
        clickElement(waitUntilClickable(locator))
    }
}