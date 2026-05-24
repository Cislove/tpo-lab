package tpo.lab3.tests

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.openqa.selenium.WebDriver
import tpo.lab3.pages.AuthPage
import tpo.lab3.pages.HomePage
import tpo.lab3.pages.ProfilPage

class AuthTest: BehaviorSpec(), BaseTest {
    override lateinit var driver: WebDriver
    lateinit var homePage: HomePage
    lateinit var authPage: AuthPage
    lateinit var profilPage: ProfilPage

    init {
        beforeSpec {
            createDriver()
            homePage = HomePage(driver)
            authPage = AuthPage(driver)
            profilPage = ProfilPage(driver)
        }


        afterSpec {
            quitDriver()
        }

        Given("Пользователь заходит на страницу авторизации") {
            beforeContainer {
                driver.manage().deleteAllCookies()
                homePage.open()
                    .waitUntilOpened()
                    .clickLogInButton()
                    .waitUntilOpened()
            }

            When("пользователь вводит правильный логин и пароль") {
                Then("AUTH-001: после ввода логина и пароля открывается профиль") {
                    authPage.loginAs("vim1521841@yandex.ru", "C5U_DwuPP7iVpr;")
                    profilPage.waitUntilOpened()
                }
            }

            When("пользователь вводит не email и пароль") {
                Then("AUTH-002: авторизация не происходит и пользователь остаётся на странице /auth") {
                    authPage.loginAs("something", "something")
                    authPage.waitUntilOpened()
                        .errorMessage() shouldBe "Введите емейл или телефон"
                }
            }

            When("пользователь вводит не правильный логин и пароль") {
                Then("AUTH-003: авторизация не происходит и пользователь остаётся на странице /auth") {
                    authPage.loginAs("correct@yandex.ru", "something")
                    authPage.waitUntilOpened()
                        .errorMessage() shouldBe "Пользователь с таким емейлом не найден"
                }
            }

            When("пользователь вводит правильный логин и не правильный пароль") {
                Then("AUTH-004: авторизация не происходит и пользователь остаётся на странице /auth") {
                    authPage.loginAs("vim1521841@yandex.ru", "something")
                    authPage.waitUntilOpened()
                        .errorMessage() shouldContain Regex("Введённый вами пароль для адреса .* неверен")
                }
            }

            When("Пользователь аутентифицировался и нажал кнопку выйти") {
                Then("AUTH-005: пользователь попал на домашнюю страницу") {
                    authPage.waitUntilOpened()
                        .loginAs("vim1521841@yandex.ru", "C5U_DwuPP7iVpr;")

                    profilPage.waitUntilOpened()
                        .clickLogOutButton()
                        .waitUntilOpened()
                }
            }
        }
    }
}