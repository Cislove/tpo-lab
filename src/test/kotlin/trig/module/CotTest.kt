package trig.module

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import trig.Cos
import trig.Cot
import trig.Sin
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class CotTest : FunSpec({

    val precision = BigDecimal("0.0000001")
    val sin = Sin()
    val cos = Cos(sin)
    val cot = Cot(sin, cos)

    test("throws for x where sin(x) = 0 (cot undefined)") {
        shouldThrow<IllegalArgumentException> {
            cot.calculate(BigDecimal.ZERO, precision)
        }
    }

    context("table values (parameterized)") {
        data class Case(val x: String, val expected: String?) {
            val label: String get() = "x=$x"
        }

        val cases = listOf(
            Case("0.7853981633974483", "1"),
            Case("-0.7853981633974483", "-1"),
            Case("1.5707963267948966", "0"),
            Case("0", null),
            Case("3.141592653589793238462643383279502884", null),
        )

        withData(nameFn = { it.label }, cases) { c ->
            if (c.expected == null) {
                shouldThrow<IllegalArgumentException> {
                    cot.calculate(BigDecimal(c.x), precision)
                }
            } else {
                val expected = BigDecimal(c.expected).setScale(precision.scale(), HALF_EVEN)
                cot.calculate(BigDecimal(c.x), precision) shouldBe expected
            }
        }
    }
})

