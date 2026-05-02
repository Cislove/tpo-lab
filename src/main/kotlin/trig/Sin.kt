package trig

import function.AbstractFunction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.PI

class Sin: AbstractFunction() {

    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val outScale = accuracy.scale()
        val mc = MathContext(outScale + 10, RoundingMode.HALF_EVEN)

        val pi = BigDecimal(PI).round(mc)
        val tau = pi.multiply(BigDecimal("2"), mc)

        var normalizedX = x.remainder(tau, mc)
        if (normalizedX > pi) {
            normalizedX = normalizedX.subtract(tau, mc)
        } else if (normalizedX < pi.negate()) {
            normalizedX = normalizedX.add(tau, mc)
        }

        var result = normalizedX
        var term = normalizedX
        val x2 = normalizedX.multiply(normalizedX, mc)

        var i = 1
        while (i < seriesLength) {
            val denom = BigDecimal.valueOf((2L * i) * (2L * i + 1L))
            term = term.multiply(x2, mc).divide(denom, mc)

            result = if (i % 2 == 1) {
                result.subtract(term, mc)
            } else {
                result.add(term, mc)
            }

            if (term.abs() <= accuracy) break
            i++
        }

        return result.setScale(outScale, RoundingMode.HALF_EVEN)
    }

}