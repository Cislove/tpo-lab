import java.lang.Math.ulp
import kotlin.math.IEEErem
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.tan
import kotlin.math.withSign

fun tg(x: Double, n: Int = 20): Double {
    if (x.isNaN() || x.isInfinite()) return Double.NaN
    if (x == 0.0) return 0.0.withSign(x)
    if (n <= 0) return 0.0

    var normalX = x.IEEErem(PI)

    if(abs(abs(normalX) - PI / 2.0) <= 1e-12) {
        return Double.POSITIVE_INFINITY.withSign(x)
    }

    val isNegative = normalX < 0.0
    normalX = abs(normalX)

    var countOfApprox = 0
    while (normalX > (PI / 16.0)) {
        normalX *= 0.5
        countOfApprox++
    }

    var t = computeTanSeries(normalX, n)

    repeat(countOfApprox) {
        val denom = 1.0 - t * t
        if (abs(denom) < 1e-15) {
            return if(isNegative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
        }
        t = (2.0 * t) / denom
    }

    return if(isNegative) -t else t
}

private fun computeTanSeries(x: Double, n: Int): Double {
    if (n <= 0 || x == 0.0) return 0.0

    val c = DoubleArray(n + 1)
    c[1] = 1.0
    for (i in 2..n) {
        var sum = 0.0
        for (j in 1 until i) {
            sum += c[j] * c[i - j]
        }
        c[i] = sum / (2 * i - 1)
    }

    var result = 0.0
    var xCurrent = x

    for (i in 1..n) {
        val temp = c[i] * xCurrent
        result += temp

        if (i > 5 && abs(temp) < ulp(result) * 10.0) {
            break
        }

        if (i< n) {
            xCurrent *= x * x
        }
    }
    return result
}

fun main(args: Array<String>) {
    require(args.isNotEmpty()) { "Use <x> <n (optional)>" }
    val x = args[0].toDouble()

    val result =
        if(args.size > 1) {
            tg(x, args[1].toInt())
        }
        else {
            tg(x)
        }

    println("tg($x) = $result\n")
    println("Math.tg($x) = ${tan(x)}")
}