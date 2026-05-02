package function

import log.BaseNLogarithm
import log.NaturalLogarithm
import trig.Cot
import trig.Csc
import trig.Sec
import java.math.BigDecimal
import java.math.RoundingMode

class MainFunction(
    private val cot: Cot,
    private val csc: Csc,
    private val sec: Sec,
    private val ln: NaturalLogarithm,
    private val log2: BaseNLogarithm,
    private val log3: BaseNLogarithm,
    private val log5: BaseNLogarithm,
    private val log10: BaseNLogarithm,
): AbstractFunction() {

    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val temp = accuracy.setScale(accuracy.scale() + 12, RoundingMode.HALF_EVEN)

        val result = if(x <= BigDecimal.ZERO) {
            calculateNegativeBranch(x, temp)
        } else {
            calculatePositiveBranch(x, temp)
        }

        return result.setScale(accuracy.scale(), RoundingMode.HALF_EVEN)
    }

    private fun calculateNegativeBranch(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val cscX = csc.calculate(x, accuracy)
        val cotX = cot.calculate(x, accuracy)
        val secX = sec.calculate(x, accuracy)

        return (cscX + cotX).pow(18) * (cscX.pow(2) + (secX * cotX))
    }

    private fun calculatePositiveBranch(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        val lnX = ln.calculate(x, accuracy)
        val log2X = log2.calculate(x, accuracy)
        val log3X = log3.calculate(x, accuracy)
        val log5X = log5.calculate(x, accuracy)
        val log10X = log10.calculate(x, accuracy)

        return (log5X * log5X).pow(2) / (log2X.pow(3) * log10X.pow(3)) *
                ((log5X - lnX) + (log3X * log10X.pow(2))) *
                log5X
    }
}