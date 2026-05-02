package trig

import function.AbstractFunction
import java.math.BigDecimal
import java.math.RoundingMode

class Sec(
    private val cos: Cos
): AbstractFunction() {
    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val cosX = cos.calculate(x, accuracy)

        require(cosX.abs() > accuracy) {
            "sec(x) is undefined for cos(x) -> 0"
        }

        return BigDecimal.ONE.divide(cosX, accuracy.scale(), RoundingMode.HALF_EVEN)
    }
}

class Csc(
    private val sin: Sin
) : AbstractFunction() {
    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val sinX = sin.calculate(x, accuracy)

        require(sinX.abs() > accuracy) {
            "csc(x) is undefined for sin(x) -> 0"
        }

        return BigDecimal.ONE.divide(sinX, accuracy.scale(), RoundingMode.HALF_EVEN)
    }
}

class Cot(
    private val sin: Sin,
    private val cos: Cos
) : AbstractFunction() {
    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val sinX = sin.calculate(x, accuracy)
        require(sinX.abs() > accuracy) {
            "cot(x) is undefined for sin(x) -> 0"
        }

        val cosX = cos.calculate(x, accuracy)
        return cosX.divide(sinX, accuracy.scale(), RoundingMode.HALF_EVEN)
    }
}