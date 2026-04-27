package function

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class AbstractFunctionTest : FunSpec({

    test("calculate delegates to safeCalculate for valid accuracy") {
        var called = false

        val f = object : AbstractFunction() {
            override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
                called = true
                return x
            }
        }

        shouldNotThrowAny {
            f.calculate(BigDecimal.ONE, BigDecimal("0.5"))
        }
        called shouldBe true
    }

    test("calculate should throw IllegalArgumentException for accuracy <= 0") {
        val f = object : AbstractFunction() {
            override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal = x
        }

        shouldThrow<IllegalArgumentException> {
            f.calculate(BigDecimal.ONE, BigDecimal.ZERO)
        }
    }

    test("calculate should throw IllegalArgumentException for accuracy >= 1") {
        val f = object : AbstractFunction() {
            override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal = x
        }

        shouldThrow<IllegalArgumentException> {
            f.calculate(BigDecimal.ONE, BigDecimal.ONE)
        }

        shouldThrow<IllegalArgumentException> {
            f.calculate(BigDecimal.ONE, BigDecimal("1.0"))
        }
    }
})


