package tpo.lab3.tests

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.WebDriver
import tpo.lab3.AuthFlow
import tpo.lab3.config.TestConfig
import tpo.lab3.config.WebDriverFactory
import tpo.lab3.pages.HomePage
import tpo.lab3.pages.UserProfilePage

class UserProfileTest: BehaviorSpec(), BaseTest {
    override lateinit var driver: WebDriver
    lateinit var homePage: HomePage
    lateinit var userProfilePage: UserProfilePage

    init {
        beforeSpec {
            driver = WebDriverFactory.create(TestConfig)
            homePage = HomePage(driver)
            userProfilePage = UserProfilePage(driver)
        }

        afterSpec {
            quitDriver()
        }

        Given("Пользователь авторизован") {
            beforeContainer {
                driver.manage().deleteAllCookies()
                AuthFlow.login(driver).waitUntilOpened()
            }

            When("Открывает анкету пользователя") {
                Then("ANKETA-001: анкета пользователя успешно открылась") {
                    userProfilePage = UserProfilePage(driver).open("9891")
                        .waitUntilOpened()
                    userProfilePage.currentUrl() shouldContain "/prosmotr-ankety/9891"
                }

                Then("ANKETA-002: в анкете пользователя отображаются правильные данные") {
                    userProfilePage = UserProfilePage(driver).open("9891")
                        .waitUntilOpened()
                    userProfilePage.readBirthday() shouldContain "27.02.1992 (34 года)"
                }
            }
        }
    }
}