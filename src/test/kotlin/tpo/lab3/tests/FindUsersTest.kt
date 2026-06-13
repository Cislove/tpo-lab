package tpo.lab3.tests

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.print.printWithType
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.LogLevel
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.test.logging.LogEntry
import io.kotest.engine.test.logging.LogExtension
import io.kotest.engine.test.logging.TestLogger
import io.kotest.engine.test.logging.warn
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.ints.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.mpp.log
import org.openqa.selenium.WebDriver
import tpo.lab3.AuthFlow
import tpo.lab3.pages.FindUsersPage
import tpo.lab3.pages.UserProfilePage

class FindUsersTest : BehaviorSpec(), BaseTest {
    override lateinit var driver: WebDriver
    lateinit var findUsersPage: FindUsersPage

    init {
        beforeSpec {
            createDriver()
        }

        afterSpec {
            quitDriver()
        }

        Given("Пользователь авторизован и находится на странице поиска анкет") {
            beforeContainer {
                driver.manage().deleteAllCookies()
                AuthFlow.login(driver)
                    .waitUntilOpened()
                findUsersPage = FindUsersPage(driver)
                    .open()
                    .waitUntilOpened()
                findUsersPage.selectSex("f")
            }

            When("Пользователь выбирает пол и страну для поиска") {
                beforeContainer {
                    findUsersPage
                        .selectCountry("1")
                        .clickSearchButton()
                }

                Then("SEARCH-001: Выводится список анкет, соответствующих полу и стране") {
                    verifySearchResults(russiaWoman) {
                        assertSoftly {
                            readCountry() shouldBeEqual "Россия"
                            readGender() shouldBeEqual "Женщина"
                        }
                    }
                }
            }

            When("Пользователь фильтрует анкеты по области и городу") {
                beforeContainer {
                    findUsersPage.selectCountry("1")
                        .selectRegion("47")
                        .selectCity("1060")
                        .selectOrderBy("reg_date")
                        .clickSearchButton()
                }

                Then("SEARCH-002: Выводится список анкет из Санкт-Петербурга") {
                    verifySearchResults(russiaSpbWoman) {
                        assertSoftly {
                            readCountry() shouldBeEqual "Россия"
                            readCity() shouldBeEqual "Санкт-Петербург"
                            readGender() shouldBeEqual "Женщина"
                        }
                    }
                }
            }

            When("Пользователь указывает диапазон возраста") {
                beforeContainer {
                    findUsersPage.enterAgeMin("18")
                        .enterAgeMax("25")
                        .selectOrderBy("reg_date")
                        .clickSearchButton()
                }

                Then("SEARCH-003: Выводится список анкет в возрастном диапазоне от 18 до 25 лет") {
                    verifySearchResults(from18to25woman) {
                        val age = readAge().toInt()
                        assertSoftly {
                            age shouldBeGreaterThanOrEqualTo 18
                            age shouldBeLessThanOrEqualTo 25

                            readGender() shouldBeEqual "Женщина"
                        }
                    }
                }
            }

            When("Пользователь указывает диапазон роста") {
                beforeContainer {
                    findUsersPage.enterHeightMin("160")
                        .enterHeightMax("180")
                        .selectOrderBy("reg_date")
                        .clickSearchButton()
                }

                Then("SEARCH-004: Выводится список анкет с ростом от 160 до 180 см") {
                    verifySearchResults(from160to180woman) {
                        val height = readHeight().subSequence(0, 3).toString().toInt()
                        assertSoftly {
                            height shouldBeGreaterThanOrEqualTo 160
                            height shouldBeLessThanOrEqualTo 180

                            readGender() shouldBeEqual "Женщина"
                        }
                    }
                }
            }

            When("Пользователь выполняет сложный поиск со всеми фильтрами") {
                beforeContainer {
                    findUsersPage
                        .selectCountry("1")
                        .selectRegion("47")
                        .selectCity("1060")
                        .enterAgeMin("20")
                        .enterAgeMax("30")
                        .enterHeightMin("165")
                        .enterHeightMax("175")
                        .clickSearchButton()
                }

                Then("SEARCH-005: Выводятся анкеты, полностью соответствующие всем критериям поиска") {
                    verifySearchResults(fullSearchTest) {
                        val age = readAge().toInt()
                        val height = readHeight().toInt()

                        assertSoftly{
                            readCountry() shouldBeEqual "Россия"
                            readCity() shouldBeEqual "Санкт-Петербург"

                            age shouldBeGreaterThanOrEqualTo 20
                            age shouldBeLessThanOrEqualTo 30

                            height shouldBeGreaterThanOrEqualTo 165
                            height shouldBeLessThanOrEqualTo 175

                            readGender() shouldBeEqual "Женщина"
                        }
                    }
                }
            }
        }
    }

    companion object IdsForSearchTest {
        val russiaWoman: List<String> = listOf("13388", "13377", "13360", "13332")
        val russiaSpbWoman: List<String> = listOf("13332", "13107", "13066", "13022")
        val from18to25woman: List<String> = listOf("13314", "13288", "13230", "13139")
        val from160to180woman: List<String> = russiaWoman
        val fullSearchTest: List<String> = listOf("13332", "11492", "9522", "8549")
    }

    private fun verifySearchResults(
        expectedIds: List<String>,
        verifyProfile: UserProfilePage.() -> Unit
    ) {
        assertSoftly {
            expectedIds.forEach { id ->
                findUsersPage.isUserCardPresent(id) shouldBe true
            }
        }

        expectedIds.forEach { id ->
            findUsersPage.clickUserProfile(id)

            val userProfilePage = UserProfilePage(driver)
                .open(id)
                .waitUntilOpened()

            userProfilePage.apply(verifyProfile)

            driver.navigate().back()
            findUsersPage.waitUntilOpened()
        }
    }

}

