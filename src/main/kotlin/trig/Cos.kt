package trig

import function.AbstractFunction
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import kotlin.math.PI

class Cos(
    private val sin: Sin = Sin()
) : AbstractFunction() {

    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val mc = MathContext(accuracy.scale() + 2, RoundingMode.HALF_EVEN)
        val halfPi = BigDecimal(PI).round(mc).divide(BigDecimal(2), mc)

        return sin.calculate(halfPi.subtract(x), accuracy)
    }
}