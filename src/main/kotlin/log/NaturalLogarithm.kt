package log

import function.AbstractFunction
import java.math.BigDecimal
import java.math.RoundingMode

class NaturalLogarithm: AbstractFunction() {
    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        require(x.signum() > 0) { "In ln(x), x must be positive" }
        if(x.compareTo(BigDecimal.ONE) == 0) {
            return BigDecimal.ZERO.setScale(accuracy.scale(), RoundingMode.HALF_EVEN)
        }

        val calcScale = accuracy.scale() + 10
        val calcAccuracy = BigDecimal.ONE.movePointLeft(calcScale)
        val z = x.subtract(BigDecimal.ONE).divide(x.add(BigDecimal.ONE), calcScale, RoundingMode.HALF_EVEN)
        var result = BigDecimal.ZERO
        var iterations = 1

        do {
            val term = z.pow(iterations)
            result = result.add(
                term.divide(BigDecimal.valueOf(iterations.toLong()), calcScale, RoundingMode.HALF_EVEN)
            )
            iterations += 2
        } while (term.abs() > calcAccuracy && iterations < seriesLength)

        return result.multiply(BigDecimal.valueOf(2)).setScale(accuracy.scale(), RoundingMode.HALF_EVEN)
    }
}