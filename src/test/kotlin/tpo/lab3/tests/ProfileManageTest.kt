package tpo.lab3.tests

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.WebDriver
import tpo.lab3.AuthFlow
import tpo.lab3.pages.HomePage
import tpo.lab3.pages.ProfilePage

class ProfileManageTest: BehaviorSpec(), BaseTest {
    override lateinit var driver: WebDriver
    lateinit var homePage: HomePage
    lateinit var profilePage: ProfilePage
    lateinit var editProfilePage: ProfilePage.EditProfilPage

    init {
        beforeSpec {
            createDriver()
            homePage = HomePage(driver)
            profilePage = ProfilePage(driver)
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
                    profilePage.waitUntilOpened()
                }

                Then("PROF-001: открывается страница профиля") {
                    driver.currentUrl shouldContain "/profil"
                }

                When("нажимает кнопку редактирования профиля") {
                    beforeContainer {
                        editProfilePage = profilePage.clickEditProfilButton()
                            .open()
                            .waitUntilOpened()
                    }

                    Then("PROF-002: видит страницу редактирования") {
                        driver.currentUrl shouldContain "/profil"
                    }

                    When("пользователь редактирует имя в крещении") {
                        beforeContainer {
                            editProfilePage.setBaptismName("NeIvan")
                                .waitUpdatedVisible()

                            driver.navigate().refresh()
                        }
                        Then("PROF-003: имя в крещении меняется") {
                            editProfilePage.setBaptismName("Ivan")
                                .waitUpdatedVisible()

                            driver.navigate().refresh()
                            editProfilePage.readBaptismName() shouldBe "Ivan"
                        }
                    }
                }
            }
        }
    }
}
