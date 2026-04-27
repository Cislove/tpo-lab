import function.MathFunction
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.io.File
import java.math.BigDecimal

class CsvFileWriterTest : FunSpec({
    lateinit var tempFile: File
    lateinit var writer: СsvFileWriter

    beforeEach {
        tempFile = kotlin.io.path.createTempFile(prefix = "csvwriter-", suffix = ".csv").toFile()
        tempFile.deleteOnExit()
        writer = СsvFileWriter(tempFile)
    }

    test("write writes header and values for range with step") {
        val f = object : MathFunction {
            override fun calculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal =
                x.add(BigDecimal.ONE)
        }

        writer.write(
            startX = BigDecimal("2"),
            endX = BigDecimal("3"),
            step = BigDecimal("0.5"),
            function = f,
            accuracy = BigDecimal("0.001")
        )

        val lines = tempFile.readLines()
        assertSoftly {
            lines.size shouldBe 1 + 2
            lines[0] shouldBe "x, y"
            lines[1] shouldBe "2, 3"
            lines[2] shouldBe "2.5, 3.5"
        }
    }

    test("write writes Nan when function throws IllegalArgumentException") {
        val f = object : MathFunction {
            override fun calculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
                throw IllegalArgumentException("bad x")
            }
        }

        writer.write(
            startX = BigDecimal.ZERO,
            endX = BigDecimal.ONE,
            step = BigDecimal("0.5"),
            function = f,
            accuracy = BigDecimal("0.001")
        )

        val lines = tempFile.readLines()
        assertSoftly {
        lines[0] shouldBe "x, y"
        lines[1] shouldBe "0, Nan"
        lines[2] shouldBe "0.5, Nan"
            }
    }

    test("write flushes even if function throws non-IllegalArgumentException") {
        val f = object : MathFunction {
            override fun calculate(x: BigDecimal, accuracy: BigDecimal): BigDecimal {
                throw ArithmeticException("boom")
            }
        }

        shouldThrow<ArithmeticException> {
            writer.write(
                startX = BigDecimal.ZERO,
                endX = BigDecimal.ONE,
                step = BigDecimal("0.5"),
                function = f,
                accuracy = BigDecimal("0.001")
            )
        }

        val lines = tempFile.readLines()
        lines[0] shouldBe "x, y"
    }
})

