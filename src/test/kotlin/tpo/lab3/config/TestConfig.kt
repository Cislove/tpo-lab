package tpo.lab3.config

object TestConfig {
    val baseUrl: String = System.getProperty("baseUrl", "https://azbyka.ru/znakomstva/")
    val browserName: String = System.getProperty("browserName", "chrome").lowercase()
    val headless: Boolean = System.getProperty("headless", "false").toBooleanStrictOrNull() ?: false
}