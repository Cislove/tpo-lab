package trig.module

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.bigdecimal.shouldBeLessThan
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import trig.Sin
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class SinTest : FunSpec({

    val sin = Sin()
    val precision = BigDecimal("0.0000001")

    test("sin(0) = 0 with given scale") {
        sin.calculate(BigDecimal.ZERO, precision) shouldBe BigDecimal.ZERO.setScale(precision.scale(), HALF_EVEN)
    }

    context("table values (parameterized)") {
        data class Case(val x: String, val expected: String) {
            val label: String get() = "x=$x"
        }

        val cases = listOf(
            Case("0", "0"),
            Case("3.141592653589793238462643383279502884", "0"),
            Case("-3.141592653589793238462643383279502884", "0"),
            Case("1.5707963267948966", "1"),
            Case("-1.5707963267948966", "-1"),
        )

        withData(nameFn = { it.label }, cases) { c ->
            val actual = sin.calculate(BigDecimal(c.x), precision)
            val expected = BigDecimal(c.expected).setScale(precision.scale(), HALF_EVEN)
            actual shouldBe expected
        }
    }

    test("periodicity: sin(x + 2*pi) = sin(x) for one table x") {
        val x = BigDecimal("0.3")
        val twoPi = BigDecimal("6.283185307179586")

        val a = sin.calculate(x, precision)
        val b = sin.calculate(x.add(twoPi), precision)

        a shouldBe b
    }
})

