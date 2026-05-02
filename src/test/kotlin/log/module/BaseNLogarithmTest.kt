package log.module

import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import log.BaseNLogarithm
import log.NaturalLogarithm

class BaseNLogarithmTest : FunSpec({

    val precision = BigDecimal("0.0000001")
    val ln = NaturalLogarithm()

    val log5 = BaseNLogarithm(BigDecimal("5"), ln)
    val defaultLog = BaseNLogarithm(BigDecimal.TEN, ln)

    test("shouldNotCalculateForZero") {
        shouldThrow<IllegalArgumentException> {
            log5.calculate(BigDecimal.ZERO, precision)
        }
    }

    test("shouldThrowForNegativeX") {
        val x = BigDecimal("-1")

        val exception = shouldThrow<IllegalArgumentException> {
            log5.calculate(x, precision)
        }

        exception.message shouldBe "In log(x), x must be positive"
    }

    test("shouldCalculateForOne") {
        log5.calculate(BigDecimal.ONE, precision) shouldBe BigDecimal.ZERO.setScale(7, HALF_EVEN)
    }

    test("shouldUseDefaultBaseTenConstructor") {
        defaultLog.calculate(BigDecimal.TEN, precision) shouldBe BigDecimal.ONE.setScale(7, HALF_EVEN)
    }
    context("table values (parameterized)") {
        data class Case(val base: BigDecimal, val x: BigDecimal, val expected: BigDecimal) {
            val label: String get() = "base=$base x=$x"
        }

        val cases = listOf(
            Case(BigDecimal("2"), BigDecimal("0.5"), BigDecimal("-1")),
            Case(BigDecimal("2"), BigDecimal("1"), BigDecimal("0")),
            Case(BigDecimal("2"), BigDecimal("2"), BigDecimal("1")),
            Case(BigDecimal("2"), BigDecimal("4"), BigDecimal("2")),

            Case(BigDecimal("3"), BigDecimal("0.3333333333333333"), BigDecimal("-1")),
            Case(BigDecimal("3"), BigDecimal("1"), BigDecimal("0")),
            Case(BigDecimal("3"), BigDecimal("3"), BigDecimal("1")),
            Case(BigDecimal("3"), BigDecimal("9"), BigDecimal("2")),

            Case(BigDecimal("5"), BigDecimal("0.2"), BigDecimal("-1")),
            Case(BigDecimal("5"), BigDecimal("1"), BigDecimal("0")),
            Case(BigDecimal("5"), BigDecimal("5"), BigDecimal("1")),
            Case(BigDecimal("5"), BigDecimal("25"), BigDecimal("2")),

            Case(BigDecimal("10"), BigDecimal("0.1"), BigDecimal("-1")),
            Case(BigDecimal("10"), BigDecimal("1"), BigDecimal("0")),
            Case(BigDecimal("10"), BigDecimal("10"), BigDecimal("1")),
            Case(BigDecimal("10"), BigDecimal("100"), BigDecimal("2")),
        )

        withData(nameFn = { it.label }, cases) { c ->
            val logb = BaseNLogarithm(c.base, ln)
            logb.calculate(c.x, precision) shouldBe c.expected.setScale(precision.scale(), HALF_EVEN)
        }
    }
})

