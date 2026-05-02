package log.module

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.bigdecimal.shouldBeGreaterThan
import io.kotest.matchers.bigdecimal.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll
import log.NaturalLogarithm
import java.math.BigDecimal
import java.math.RoundingMode

class NaturalLogarithmTest : FunSpec({

    val ln = NaturalLogarithm()

    test("table: one x for ln(x) < 0, one for ln(x) = 0, one for ln(x) > 0") {
        val acc = BigDecimal("0.000001")

        withClue("ln(0.5) < 0") {
            ln.calculate(BigDecimal("0.5"), acc) shouldBeLessThan BigDecimal.ZERO
        }
        withClue("ln(1) = 0") {
            ln.calculate(BigDecimal.ONE, acc) shouldBe BigDecimal.ZERO.setScale(acc.scale())
        }
        withClue("ln(2) > 0") {
            ln.calculate(BigDecimal("2"), acc) shouldBeGreaterThan BigDecimal.ZERO
        }
    }

    test("ln(x) throws for x <= 0") {
        shouldThrow<IllegalArgumentException> {
            ln.calculate(BigDecimal.ZERO, BigDecimal("0.001"))
        }
        shouldThrow<IllegalArgumentException> {
            ln.calculate(BigDecimal("-1"), BigDecimal("0.001"))
        }
    }

    test("ln(1) is 0 with given scale") {
        val result = ln.calculate(BigDecimal.ONE, BigDecimal("0.001"))
        result shouldBe BigDecimal("0.000")
    }

    test("ln(x*y) ≈ ln(x)+ln(y) for x,y in (0,1] (within tolerance)") {
        val acc = BigDecimal("0.000001")
        val x = BigDecimal("0.7")
        val y = BigDecimal("0.6")

        val lhs = ln.calculate(x.multiply(y), acc)
        val rhs = ln.calculate(x, acc).add(ln.calculate(y, acc))

        val tolerance = acc.multiply(BigDecimal("5"))
        (lhs.subtract(rhs)).abs() shouldBeLessThan tolerance
    }

    test("property: ln(x) + ln(1/x) ≈ 0 (within tolerance)") {
        val acc = BigDecimal("0.000001")
        val tolerance = acc.multiply(BigDecimal("15"))

        checkAll(Arb.double(0.2, 5.0)) { x ->
            val bx = BigDecimal.valueOf(x)
            val inv = BigDecimal.ONE.divide(bx, acc.scale() + 10, RoundingMode.HALF_EVEN)
            val sum = ln.calculate(bx, acc).add(ln.calculate(inv, acc))
            sum.abs() shouldBeLessThan tolerance
        }
    }
})