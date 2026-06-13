package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory

class ProfilePage(
    webDriver: WebDriver,
): UserProfilePage(webDriver) {

    init {
        PageFactory.initElements(webDriver, this)
        super.id = "4834"
    }

    private val logOutButton = By.xpath("//a[contains(@href, 'action=logout')]")
    private val editProfilButton = By.xpath("//a[contains(@class, 'profile-edit-link')]")

    fun open(): ProfilePage {
        webDriver.navigate().to(baseUrl)
        return this
    }

    override fun waitUntilOpened(id: String): ProfilePage {
        super.waitUntilOpened(id)
        waitUntilClickable(logOutButton)
        return this
    }

    fun clickLogOutButton(): HomePage {
        clickElement(waitUntilClickable(logOutButton))
        return HomePage(webDriver)
    }

    fun clickEditProfilButton(): EditProfilPage {
        clickElement(waitUntilClickable(editProfilButton))
        return EditProfilPage(webDriver)
    }

    inner class EditProfilPage(
        webDriver: WebDriver = this@ProfilePage.webDriver,
        val baseUrl: String = this@ProfilePage.baseUrl + "?edit=1",
    ) : BasePage(webDriver) {

        init {
            PageFactory.initElements(webDriver, this)
        }

        private val baptismName = By.xpath("//article[@id='post-579']/div/form/div/div[2]/div/div[6]/div/input")
        private val saveBar = By.xpath("//span[@id='saving_message']")

        fun open(): EditProfilPage {
            webDriver.navigate().to(this.baseUrl)
            return this
        }

        fun waitUpdatedVisible() {
            waitUntilVisible(saveBar)
            wait.until { webDriver ->
                val el = webDriver.findElement(saveBar)
                el.isDisplayed && el.text.contains("Анкета сохраняется...")
            }
            wait.until { webDriver ->
                val el = webDriver.findElement(saveBar)
                el.isDisplayed && el.text.contains("Анкета сохранена!")
            }
        }

        fun waitUntilOpened(): EditProfilPage {
            waitUntilUrlContains("edit=1")
            waitUntilVisible(baptismName)
            return this
        }

        fun setBaptismName(text: String): EditProfilPage {
            val el = waitUntilVisible(baptismName)
            el.clear()
            el.sendKeys(text + text.last())
            return this
        }

        fun readBaptismName(): String = waitUntilVisible(baptismName).getAttribute("value") ?: ""
    }
}