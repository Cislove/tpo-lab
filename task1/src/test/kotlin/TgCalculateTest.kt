import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.double
import io.kotest.property.checkAll
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

class TgCalculateTest : StringSpec({

    //тестим по точкам, дабы убедиться в работоспособности тангенса как функции
    "check corner dots" {
        forAll(
            row(PI / 6, 1 / sqrt(3.0)),
            row(PI / 4, 1.0),
            row(PI / 3, sqrt(3.0)),
        ) { x, expected ->
            tg(x) shouldBe (expected plusOrMinus EPS)
        }
    }

    "check asymptotes" {
        forAll(
            row(PI / 2.0 - 1e-13, Double.POSITIVE_INFINITY),
            row(-PI / 2.0 + 1e-13, Double.NEGATIVE_INFINITY),
            row(PI / 2.0 + 1e-13, Double.POSITIVE_INFINITY),
            row(-PI / 2.0 - 1e-13, Double.NEGATIVE_INFINITY)
        ) { x, expected ->
            tg(x) shouldBe expected
        }
    }

    //тестим особенности работы с числами в компутере
    "check true sign for zero" {
        (1.0 / tg(-0.0)) shouldBe Double.NEGATIVE_INFINITY
        (1.0 / tg(0.0)) shouldBe Double.POSITIVE_INFINITY
    }

    "check extreme cases" {
        tg(Double.NaN).shouldBeNaN()
        tg(Double.POSITIVE_INFINITY).shouldBeNaN()
        tg(Double.NEGATIVE_INFINITY).shouldBeNaN()
    }

    "check for large multiples" {
        val x = 1.23456789
        val k = 1_000_000.0
        tg(k) shouldNotBe (tg(k + x) plusOrMinus EPS)
    }

    //тестим свойства тангенса как математической функции с помощью проперти бейзд
    "check symmetry of tg" {
        checkAll(iterations = 100, Arb.double(min = -1e6, max = 1e6)) { x ->
            if(closeToAsymptote(x)) return@checkAll
            tg(-x) shouldBe (-tg(x) plusOrMinus EPS)
        }
    }

    "check periodicity of tg" {
        checkAll(iterations = 100, Arb.double(min = 0.0, max = 1e6)) { x ->
            if(closeToAsymptote(x)) return@checkAll
            tg(x + PI) shouldBe (tg(x) plusOrMinus EPS)
        }
    }

    "check small tg(x) approx x" {
        checkAll(iterations = 100, Arb.double(min = 0.0, max = 1.0)) { value ->
            val x = 10.0.pow(-12.0 + 9.0 * value)
            tg(x) shouldBe (x plusOrMinus EPS)
        }
    }
})

fun closeToAsymptote(x: Double) =
    abs(x - ((round(x / PI) + 0.5) * PI)) < 1e-6

private const val EPS = 1e-7