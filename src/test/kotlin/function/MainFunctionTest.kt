package function

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal

class MainFunctionTest : FunSpec({

	val accuracy = BigDecimal("0.001")

	test("правильно определяет негативную ветку (x <= 0)") {
		val cot = mock<trig.Cot>()
		val csc = mock<trig.Csc>()
		val sec = mock<trig.Sec>()
		val ln = mock<log.NaturalLogarithm>()
		val log2 = mock<log.BaseNLogarithm>()
		val log3 = mock<log.BaseNLogarithm>()
		val log5 = mock<log.BaseNLogarithm>()
		val log10 = mock<log.BaseNLogarithm>()

		whenever(csc.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(cot.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(sec.calculate(any(), any())).thenReturn(BigDecimal.ONE)

		val f = MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)

		shouldNotThrowAny {
			f.calculate(BigDecimal.ZERO, accuracy)
		}

		verify(csc).calculate(any(), any())
		verify(cot).calculate(any(), any())
		verify(sec).calculate(any(), any())

		verify(ln, never()).calculate(any(), any())
		verify(log2, never()).calculate(any(), any())
		verify(log3, never()).calculate(any(), any())
		verify(log5, never()).calculate(any(), any())
		verify(log10, never()).calculate(any(), any())
	}

	test("правильно определяет позитивную ветку (x > 0)") {
		val cot = mock<trig.Cot>()
		val csc = mock<trig.Csc>()
		val sec = mock<trig.Sec>()
		val ln = mock<log.NaturalLogarithm>()
		val log2 = mock<log.BaseNLogarithm>()
		val log3 = mock<log.BaseNLogarithm>()
		val log5 = mock<log.BaseNLogarithm>()
		val log10 = mock<log.BaseNLogarithm>()

		whenever(ln.calculate(any(), any())).thenReturn(BigDecimal.ZERO)
		whenever(log2.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(log3.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(log5.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(log10.calculate(any(), any())).thenReturn(BigDecimal.ONE)

		val f = MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)

		shouldNotThrowAny {
			f.calculate(BigDecimal("2"), accuracy)
		}

		verify(ln).calculate(any(), any())
		verify(log2).calculate(any(), any())
		verify(log3).calculate(any(), any())
		verify(log5).calculate(any(), any())
		verify(log10).calculate(any(), any())

		verify(csc, never()).calculate(any(), any())
		verify(cot, never()).calculate(any(), any())
		verify(sec, never()).calculate(any(), any())
	}

	test("выкидывает ошибку в негативной ветке") {
		val cot = mock<trig.Cot>()
		val csc = mock<trig.Csc>()
		val sec = mock<trig.Sec>()
		val ln = mock<log.NaturalLogarithm>()
		val log2 = mock<log.BaseNLogarithm>()
		val log3 = mock<log.BaseNLogarithm>()
		val log5 = mock<log.BaseNLogarithm>()
		val log10 = mock<log.BaseNLogarithm>()

		whenever(csc.calculate(any(), any())).thenThrow(IllegalArgumentException("boom"))

		val f = MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)

		shouldThrow<IllegalArgumentException> {
			f.calculate(BigDecimal("-1"), accuracy)
		}
	}

	test("выкидывает ошибку в позитивной ветке") {
		val cot = mock<trig.Cot>()
		val csc = mock<trig.Csc>()
		val sec = mock<trig.Sec>()
		val ln = mock<log.NaturalLogarithm>()
		val log2 = mock<log.BaseNLogarithm>()
		val log3 = mock<log.BaseNLogarithm>()
		val log5 = mock<log.BaseNLogarithm>()
		val log10 = mock<log.BaseNLogarithm>()

		whenever(ln.calculate(any(), any())).thenThrow(IllegalArgumentException("boom"))

		val f = MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)

		shouldThrow<IllegalArgumentException> {
			f.calculate(BigDecimal("2"), accuracy)
		}
	}

	test("не выкидывает ошибку в негативной ветке") {
		val cot = mock<trig.Cot>()
		val csc = mock<trig.Csc>()
		val sec = mock<trig.Sec>()
		val ln = mock<log.NaturalLogarithm>()
		val log2 = mock<log.BaseNLogarithm>()
		val log3 = mock<log.BaseNLogarithm>()
		val log5 = mock<log.BaseNLogarithm>()
		val log10 = mock<log.BaseNLogarithm>()

		whenever(csc.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(cot.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(sec.calculate(any(), any())).thenReturn(BigDecimal.ONE)

		val f = MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)

		shouldNotThrowAny {
			f.calculate(BigDecimal("-1"), accuracy)
		}
	}

	test("не выкидывает ошибку в позитивной ветке") {
		val cot = mock<trig.Cot>()
		val csc = mock<trig.Csc>()
		val sec = mock<trig.Sec>()
		val ln = mock<log.NaturalLogarithm>()
		val log2 = mock<log.BaseNLogarithm>()
		val log3 = mock<log.BaseNLogarithm>()
		val log5 = mock<log.BaseNLogarithm>()
		val log10 = mock<log.BaseNLogarithm>()

		whenever(ln.calculate(any(), any())).thenReturn(BigDecimal.ZERO)
		whenever(log2.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(log3.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(log5.calculate(any(), any())).thenReturn(BigDecimal.ONE)
		whenever(log10.calculate(any(), any())).thenReturn(BigDecimal.ONE)

		val f = MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)

		shouldNotThrowAny {
			f.calculate(BigDecimal("2"), accuracy)
		}
	}
})

