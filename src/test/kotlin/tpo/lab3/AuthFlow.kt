package tpo.lab3

import org.openqa.selenium.WebDriver
import tpo.lab3.pages.AuthPage
import tpo.lab3.pages.HomePage
import tpo.lab3.pages.ProfilPage

object AuthFlow {
    fun login(driver: WebDriver): ProfilPage {
        HomePage(driver)
            .open()
            .waitUntilOpened()
            .clickLogInButton()
            .waitUntilOpened()
            .loginAs("vim1521841@yandex.ru", "C5U_DwuPP7iVpr;")

        return ProfilPage(driver)
    }
}