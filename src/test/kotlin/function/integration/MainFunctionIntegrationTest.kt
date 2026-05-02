package function.integration

import function.MainFunction
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import log.BaseNLogarithm
import log.NaturalLogarithm
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import trig.Cot
import trig.Csc
import trig.Sec
import java.math.BigDecimal
import java.math.RoundingMode

class MainFunctionIntegrationTest : FunSpec({

    val precision = BigDecimal("0.0000001")
    val intermediatePrecision = precision.setScale(precision.scale() + 12, RoundingMode.HALF_EVEN)

    test("shouldCalculateRightBranch") {
        val x = BigDecimal("2.0")

        val cotMock = mock<Cot>()
        val cscMock = mock<Csc>()
        val secMock = mock<Sec>()
        val lnMock = mock<NaturalLogarithm>()
        val log2Mock = mock<BaseNLogarithm>()
        val log3Mock = mock<BaseNLogarithm>()
        val log5Mock = mock<BaseNLogarithm>()
        val log10Mock = mock<BaseNLogarithm>()

        val mainFunction = MainFunction(
            cotMock, cscMock, secMock,
            lnMock, log2Mock, log3Mock, log5Mock, log10Mock
        )

        whenever(lnMock.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO)
        whenever(log2Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ONE)
        whenever(log3Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ONE)
        whenever(log5Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ONE)
        whenever(log10Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ONE)

        val result = mainFunction.calculate(x, precision)

        result shouldBe BigDecimal("2.0000000")

        verify(lnMock).calculate(eq(x), eq(intermediatePrecision))
        verify(log2Mock).calculate(eq(x), eq(intermediatePrecision))
        verify(log3Mock).calculate(eq(x), eq(intermediatePrecision))
        verify(log5Mock).calculate(eq(x), eq(intermediatePrecision))
        verify(log10Mock).calculate(eq(x), eq(intermediatePrecision))
    }

    test("shouldThrowExceptionAt1") {
        val x = BigDecimal.ONE

        val cotMock = mock<Cot>()
        val cscMock = mock<Csc>()
        val secMock = mock<Sec>()
        val lnMock = mock<NaturalLogarithm>()
        val log2Mock = mock<BaseNLogarithm>()
        val log3Mock = mock<BaseNLogarithm>()
        val log5Mock = mock<BaseNLogarithm>()
        val log10Mock = mock<BaseNLogarithm>()

        val mainFunction = MainFunction(
            cotMock, cscMock, secMock,
            lnMock, log2Mock, log3Mock, log5Mock, log10Mock
        )

        whenever(log2Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO)
        whenever(log10Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO)
        whenever(log5Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ONE)
        whenever(log3Mock.calculate(eq(x), any())).thenReturn(BigDecimal.ONE)
        whenever(lnMock.calculate(eq(x), any())).thenReturn(BigDecimal.ZERO)

        shouldThrow<ArithmeticException> {
            mainFunction.calculate(x, precision)
        }
    }

    test("shouldCalculateLeftBranch") {
        val x = BigDecimal("-1.0")

        val cotMock = mock<Cot>()
        val cscMock = mock<Csc>()
        val secMock = mock<Sec>()
        val lnMock = mock<NaturalLogarithm>()
        val log2Mock = mock<BaseNLogarithm>()
        val log3Mock = mock<BaseNLogarithm>()
        val log5Mock = mock<BaseNLogarithm>()
        val log10Mock = mock<BaseNLogarithm>()

        val mainFunction = MainFunction(
            cotMock, cscMock, secMock,
            lnMock, log2Mock, log3Mock, log5Mock, log10Mock
        )

        whenever(cotMock.calculate(eq(x), any())).thenReturn(BigDecimal("5.0"))
        whenever(cscMock.calculate(eq(x), any())).thenReturn(BigDecimal("1.0"))
        whenever(secMock.calculate(eq(x), any())).thenReturn(BigDecimal("2.0"))

        shouldNotThrowAny {
            mainFunction.calculate(x, precision)
        }
    }
})


