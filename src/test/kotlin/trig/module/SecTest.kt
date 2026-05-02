package trig.module

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import trig.Cos
import trig.Sec
import trig.Sin
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class SecTest : FunSpec({

	val precision = BigDecimal("0.0000001")
	val sin = Sin()
	val cos = Cos(sin)
	val sec = Sec(cos)

	test("sec(0) = 1") {
		sec.calculate(BigDecimal.ZERO, precision) shouldBe BigDecimal.ONE.setScale(precision.scale(), HALF_EVEN)
	}

	test("throws for x where cos(x)=0 (sec undefined)") {
		shouldThrow<IllegalArgumentException> {
			sec.calculate(BigDecimal("1.5707963267948966"), precision)
		}
	}

	context("table values (parameterized)") {
		data class Case(val x: String, val expected: String?) {
			val label: String get() = "x=$x"
		}

		val cases = listOf(
			Case("0", "1"),
			Case("3.141592653589793238462643383279502884", "-1"),
			Case("1.0471975511965976", "2"),
			Case("1.5707963267948966", null),
			Case("-1.5707963267948966", null),
		)

		withData(nameFn = { it.label }, cases) { c ->
			if (c.expected == null) {
				shouldThrow<IllegalArgumentException> {
					sec.calculate(BigDecimal(c.x), precision)
				}
			} else {
				val expected = BigDecimal(c.expected).setScale(precision.scale(), HALF_EVEN)
				sec.calculate(BigDecimal(c.x), precision) shouldBe expected
			}
		}
	}
})

