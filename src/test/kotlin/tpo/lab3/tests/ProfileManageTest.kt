package tpo.lab3.tests

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import tpo.lab3.AuthFlow
import tpo.lab3.pages.HomePage
import tpo.lab3.pages.ProfilPage

class ProfileManageTest: BehaviorSpec(), BaseTest {
    override lateinit var driver: WebDriver
    lateinit var homePage: HomePage
    lateinit var profilPage: ProfilPage
    lateinit var editProfilPage: ProfilPage.EditProfilPage

    init {
        beforeSpec {
            createDriver()
            homePage = HomePage(driver)
            profilPage = ProfilPage(driver)
        }

        afterSpec {
            quitDriver()
        }

        Given("Пользователь авторизован") {
            beforeContainer {
                driver.manage().deleteAllCookies()
                AuthFlow.login(driver)
            }
            When("пользователь открывает страницу профиля") {
                beforeContainer {
                    profilPage.waitUntilOpened()
                }

                Then("PROF-001: открывается страница профиля") {
                    driver.currentUrl shouldContain "/profil"
                }

                When("нажимает кнопку редактирования профиля") {
                    beforeContainer {
                        editProfilPage = profilPage.clickEditProfilButton()
                            .open()
                            .waitUntilOpened()
                    }

                    Then("PROF-002: видит страницу редактирования") {
                        driver.currentUrl shouldContain "/profil"
                    }

                    When("пользователь редактирует имя в крещении") {
                        afterContainer {
                            editProfilPage.setBaptismName("NeIvan")
                        }
                        Then("PROF-003: имя в крещении меняется") {
                            editProfilPage.setBaptismName("Ivan")
                            editProfilPage.waitUpdatedVisible()
                            driver.navigate().refresh()
                            editProfilPage.readBaptismName() shouldBe "Ivan"
                        }
                    }
                }
            }
        }
    }
}
