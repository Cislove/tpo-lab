package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory
import tpo.lab3.config.TestConfig

class ProfilPage(
    webDriver: WebDriver,
    val baseUrl: String = TestConfig.baseUrl.trimEnd('/') + "/profil",
): BasePage(webDriver) {

    init {
        PageFactory.initElements(webDriver, this)
    }

    private val logOutButton = By.xpath("//article[@id='post-579']/div/div/div/div[2]/div/a[4]")
    private val editProfilButton = By.xpath("//*[@id='post-579']/div[1]/div[1]/div[1]/div[2]/div[1]/a[3]")

    fun open(): ProfilPage {
        webDriver.navigate().to(baseUrl)
        return this
    }

    fun waitUntilOpened(): ProfilPage {
        waitUntilUrlContains("/profil")
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
        webDriver: WebDriver = this@ProfilPage.webDriver,
        val baseUrl: String = this@ProfilPage.baseUrl + "?edit=1",
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