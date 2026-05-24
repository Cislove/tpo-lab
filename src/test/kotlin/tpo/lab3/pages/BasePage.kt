package tpo.lab3.pages

import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.time.Duration

open class BasePage(
    protected val webDriver: WebDriver,
    timeout: Duration = Duration.ofSeconds(10)
) {
    protected val wait = WebDriverWait(webDriver, timeout)

    protected fun waitUntilVisible(locator: By): WebElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator))

    protected fun waitUntilClickable(locator: By): WebElement =
        wait.until(ExpectedConditions.elementToBeClickable(locator))

    protected fun waitUntilUrlContains(fragment: String) {
        wait.until { webDriver ->
            val decodedUrl = URLDecoder.decode(webDriver.currentUrl ?: "", StandardCharsets.UTF_8)
            decodedUrl.contains(fragment)
        }
    }

    protected fun clickElement(element: WebElement) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(element))
            element.click()
        } catch (_: WebDriverException) {
            (webDriver as JavascriptExecutor).executeScript("arguments[0].click();", element)
        }
    }

    protected fun findVisibleElements(locator: By): List<WebElement> {
        return wait.until { webDriver ->
            val visibleElements = webDriver.findElements(locator).filter(WebElement::isDisplayed)
            visibleElements.takeIf { it.isNotEmpty() }
        } ?: emptyList()
    }

    protected fun currentUrl(): String = webDriver.currentUrl ?: ""
}