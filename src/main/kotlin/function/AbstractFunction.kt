package function

import java.math.BigDecimal

interface MathFunction {
    fun calculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal
}

abstract class AbstractFunction : MathFunction {
    protected val seriesLength: Int = 1000

    final override fun calculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
        checkBaseValid(accuracy)
        return safeCalculate(x, accuracy)
    }

    protected abstract fun safeCalculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal

    protected fun checkBaseValid(accuracy: BigDecimal) {
        require(accuracy > BigDecimal.ZERO && accuracy < BigDecimal.ONE) {
            "Accuracy must be in range (0, 1)"
        }
    }
}