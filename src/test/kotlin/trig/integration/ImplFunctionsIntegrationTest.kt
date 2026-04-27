package trig.integration

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import trig.*
import java.math.BigDecimal
import java.math.RoundingMode.HALF_EVEN

class ImplFunctionsIntegrationTest : FunSpec({

    val precision = BigDecimal("0.0000001")
    val expectedOne = BigDecimal.ONE.setScale(precision.scale(), HALF_EVEN)

    test("Sec интеграция: вызывает Cos и Sin и возвращает 1/cos(x)") {
        val x = BigDecimal.ZERO

        val sinSpy = spy(Sin())
        val cosSpy = spy(Cos(sinSpy))

        doReturn(expectedOne).`when`(cosSpy).calculate(eq(x), eq(precision))

        val sec = Sec(cosSpy)

        sec.calculate(x, precision) shouldBe expectedOne

        verify(cosSpy, times(1)).calculate(eq(x), eq(precision))
    }

    test("Csc интеграция: вызывает Sin и возвращает 1/sin(x)") {
        val x = BigDecimal("1.5707963267948966")

        val sinSpy = spy(Sin())

        doReturn(expectedOne).`when`(sinSpy).calculate(eq(x), eq(precision))

        val csc = Csc(sinSpy)
        csc.calculate(x, precision) shouldBe expectedOne

        verify(sinSpy, times(1)).calculate(eq(x), eq(precision))
    }

    test("Cot интеграция: вызывает Sin и Cos и возвращает cos(x)/sin(x)") {
        val x = BigDecimal("0.7853981633974483")

        val sinSpy = spy(Sin())
        val cosSpy = spy(Cos(sinSpy))

        doReturn(BigDecimal("0.5")).`when`(sinSpy).calculate(eq(x), eq(precision))
        doReturn(BigDecimal("1.0")).`when`(cosSpy).calculate(eq(x), eq(precision))

        val cot = Cot(sinSpy, cosSpy)
        cot.calculate(x, precision) shouldBe BigDecimal("2.0000000")

        verify(sinSpy, times(1)).calculate(eq(x), eq(precision))
        verify(cosSpy, times(1)).calculate(eq(x), eq(precision))
    }
})

