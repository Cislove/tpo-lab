package trig.module

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import trig.Csc
import trig.Sin
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class CscTest : FunSpec({

    val precision = BigDecimal("0.0000001")
    val sin = Sin()
    val csc = Csc(sin)

    test("throws for x where sin(x) = 0 (csc undefined)") {
        shouldThrow<IllegalArgumentException> {
            csc.calculate(BigDecimal.ZERO, precision)
        }
    }

    context("table values (parameterized)") {
        data class Case(val x: String, val expected: String?) {
            val label: String get() = "x=$x"
        }

        val cases = listOf(
            Case("1.5707963267948966", "1"),
            Case("-1.5707963267948966", "-1"),
            Case("0.5235987755982989", "2"),
            Case("0", null),
            Case("3.141592653589793238462643383279502884", null),
        )

        withData(nameFn = { it.label }, cases) { c ->
            if (c.expected == null) {
                shouldThrow<IllegalArgumentException> {
                    csc.calculate(BigDecimal(c.x), precision)
                }
            } else {
                val expected = BigDecimal(c.expected).setScale(precision.scale(), HALF_EVEN)
                csc.calculate(BigDecimal(c.x), precision) shouldBe expected
            }
        }
    }
})

