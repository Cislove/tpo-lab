package log.integration

import java.math.BigDecimal
import java.math.RoundingMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class LogarithmIntegrationTest : FunSpec({

    test("shouldCallLnForXAndBaseWithSamePrecision") {
        val spyLn = spy(log.NaturalLogarithm())
        val logarithm = log.BaseNLogarithm(BigDecimal("5"), spyLn)

        val precision = BigDecimal("0.001")
        val x = BigDecimal("993")

        logarithm.calculate(x, precision)

        verify(spyLn, atLeastOnce()).calculate(x, precision)
        verify(spyLn, atLeastOnce()).calculate(BigDecimal("5"), precision)
    }

    test("shouldCalculateWithMockLn") {
        val mockLn = mock<log.NaturalLogarithm>()

        val precision = BigDecimal("0.0000001")
        val arg = BigDecimal("3621")
        val base = BigDecimal("5")

        whenever(mockLn.calculate(arg, precision)).thenReturn(BigDecimal("7.3051882"))
        whenever(mockLn.calculate(base, precision)).thenReturn(BigDecimal("1.6094379"))

        val log5 = log.BaseNLogarithm(base, mockLn)

        val expected = BigDecimal("4.5389687")
            .setScale(precision.scale(), RoundingMode.HALF_EVEN)

        log5.calculate(arg, precision) shouldBe expected
    }
})

