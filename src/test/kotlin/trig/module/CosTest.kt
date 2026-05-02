package trig.module

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldBeLessThan
import io.kotest.matchers.shouldBe
import trig.Cos
import trig.Sin
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class CosTest : FunSpec({

    val precision = BigDecimal("0.0000001")
    val sin = Sin()
    val cos = Cos(sin)

    test("cos(0) = 1 with given scale") {
        cos.calculate(BigDecimal.ZERO, precision) shouldBe BigDecimal.ONE.setScale(precision.scale(), HALF_EVEN)
    }

    context("table values (parameterized)") {
        data class Case(val x: String, val expected: String) {
            val label: String get() = "x=$x"
        }

        val cases = listOf(
            Case("0", "1"),
            Case("3.141592653589793238462643383279502884", "-1"),
            Case("-3.141592653589793238462643383279502884", "-1"),
            Case("1.5707963267948966", "0"),
            Case("-1.5707963267948966", "0"),
        )

        withData(nameFn = { it.label }, cases) { c ->
            val actual = cos.calculate(BigDecimal(c.x), precision)
            val expected = BigDecimal(c.expected).setScale(precision.scale(), HALF_EVEN)
            actual shouldBe expected
        }
    }

    test("periodicity: cos(x + 2*pi) = cos(x) for one table x") {
        val x = BigDecimal("0.3")
        val twoPi = BigDecimal("6.283185307179586")

        val a = cos.calculate(x, precision)
        val b = cos.calculate(x.add(twoPi), precision)

        a shouldBe b
    }

    test("relation: cos(x) = sin(pi/2 - x) using same implementation") {
        val x = BigDecimal("0.7")
        val piOver2 = BigDecimal("1.5707963267948966")

        val expected = sin.calculate(piOver2.subtract(x), precision)
        val actual = cos.calculate(x, precision)

        actual shouldBe expected
    }
})

