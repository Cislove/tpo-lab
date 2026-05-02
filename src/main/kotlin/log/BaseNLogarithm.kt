package log

import function.AbstractFunction
import java.math.BigDecimal
import java.math.RoundingMode

class BaseNLogarithm(
    private val base: BigDecimal,
    private val naturalLogarithm: NaturalLogarithm
): AbstractFunction() {
    private var lnBaseCache: BigDecimal? = null
    private var lnBaseAccuracy: BigDecimal? = null

    private fun lnBase(accuracy: BigDecimal): BigDecimal {
        val cached = lnBaseCache
        val cachedAccuracy = lnBaseAccuracy
        return if (cached == null || cachedAccuracy == null || cachedAccuracy != accuracy) {
            naturalLogarithm.calculate(base, accuracy).also {
                lnBaseCache = it
                lnBaseAccuracy = accuracy
            }
        } else {
            cached
        }
    }


    override fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        require(x.signum() > 0) { "In log(x), x must be positive" }

        val lnX = naturalLogarithm.calculate(x, accuracy)
        val lnBase = lnBase(accuracy)

        return lnX.divide(lnBase, accuracy.scale(), RoundingMode.HALF_EVEN)
    }
}