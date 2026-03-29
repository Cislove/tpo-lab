import io.kotest.core.spec.style.StringSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import io.kotest.matchers.doubles.shouldBeNaN
import io.kotest.matchers.shouldNotBe
import kotlin.math.PI

private const val EPS = 1e-8

class TgCalculateTest : StringSpec({

    "check corner dots" {
        forAll(
            row(0.1, 0.10033467),
            row(0.5, 0.54630249),
            row(1.0, 1.55740772),
            row(2.0, -2.18503986),
            row(3.0, -0.14254654),
            row(3.5, 0.37458564),
        ) { x, expected ->
            tg(x) shouldBe (expected plusOrMinus EPS)
        }
    }

    "check true sign for zero" {
        (1.0 / tg(-0.0)) shouldBe Double.NEGATIVE_INFINITY
        (1.0 / tg(0.0))  shouldBe Double.POSITIVE_INFINITY
    }

    "check symmetry of tg" {
        val x = 1.23456789
        tg(-x) shouldBe (-tg(x) plusOrMinus EPS)
    }

    "check periodicity of tg" {
        val x = 1.23456789
        tg(x + PI) shouldBe (tg(x) plusOrMinus EPS)
    }

    "check extreme cases" {
        tg(Double.NaN).shouldBeNaN()
        tg(Double.POSITIVE_INFINITY).shouldBeNaN()
        tg(Double.NEGATIVE_INFINITY).shouldBeNaN()
    }

    "check assymptotes" {
        forAll(
            row(PI / 2.0 - 1e-13, Double.POSITIVE_INFINITY),
            row(-PI / 2.0 + 1e-13, Double.NEGATIVE_INFINITY),
            row(PI / 2.0 + 1e-13, Double.POSITIVE_INFINITY),
            row(-PI / 2.0 - 1e-13, Double.NEGATIVE_INFINITY)
        ) { x, expected ->
            tg(x) shouldBe expected
        }
    }

    "check for large multiples" {
        val x = 1.23456789
        val k = 1_000_000.0
        tg(k) shouldNotBe (tg(k + x) plusOrMinus EPS)
    }

    "check small tg(x) approx x" {
        forAll(
            row(1e-12),
            row(1e-9),
            row(1e-6),
            row(1e-4),
            row(1e-3)
        ) {
            tg(it) shouldBe (it plusOrMinus EPS)
        }
    }
})