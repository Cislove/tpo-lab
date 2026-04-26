import function.MainFunction
import function.MathFunction
import log.BaseNLogarithm
import log.NaturalLogarithm
import trig.*
import java.io.File
import java.math.BigDecimal

fun main(args: Array<String>) {
    val options = try {
        parseOptions(args)
    } catch (e: IllegalArgumentException) {
        println("Error parsing options: ${e.message}")
        return
    } catch (e: NumberFormatException) {
        println("Bad decimal: ${e.message}")
        return
    }

    val csvWriter = СsvFileWriter(options.outputDirectory)

    val app = App(csvWriter)

    app.run(options)
    println("Csv file output: ${options.outputDirectory.absolutePath}")
}

class App(
    private val csvWriter: СsvFileWriter
) {
    private val functions: List<MathFunction>

    init {
        val sin = Sin()
        val cos = Cos()
        val ln = NaturalLogarithm()
        val sec = Sec(cos)
        val csc = Csc(sin)
        val cot = Cot(sin, cos)
        val log2 = BaseNLogarithm(BigDecimal.valueOf(2), ln)
        val log3 = BaseNLogarithm(BigDecimal.valueOf(3), ln)
        val log5 = BaseNLogarithm(BigDecimal.valueOf(5), ln)
        val log10 = BaseNLogarithm(BigDecimal.valueOf(10), ln)

        functions = listOf(
            sec,
            csc,
            cot,
            log2,
            log3,
            log5,
            log10,
            MainFunction(cot, csc, sec, ln, log2, log3, log5, log10)
        )
    }

    fun run(options: Options) {
        functions.forEach {
            csvWriter.write(
                options.rangeStart,
                options.rangeEnd,
                options.step,
                it,
                options.accuracy
            )
        }
    }
}


data class Options(
    var outputDirectory: File = File("output"),
    var accuracy: BigDecimal = BigDecimal(0.001),
    var rangeStart: BigDecimal = BigDecimal(1),
    var rangeEnd: BigDecimal = BigDecimal(1),
    var step: BigDecimal = BigDecimal(1),
)

private fun parseOptions(args: Array<String>): Options {
    require(args.size % 2 == 0) { "Arguments must be in pairs: --option value" }

    var result = Options()

    args.asList().chunked(2).forEach { (key, value) ->
        result = when (key) {
            "--outputDir" -> result.copy(outputDirectory = File(value))
            "--accuracy" -> result.copy(accuracy = value.toBigDecimalOrNull()
                ?: error("Invalid accuracy value: $value"))
            "--start" -> result.copy(rangeStart = value.toBigDecimalOrNull()
                ?: error("Invalid range start value: $value"))
            "--end" -> result.copy(rangeEnd = value.toBigDecimalOrNull()
                ?: error("Invalid range end value: $value"))
            "--step" -> result.copy(step = value.toBigDecimalOrNull()
                ?: error("Invalid step value: $value"))
            else -> error("Unknown option: $key")
        }
    }

    return result
}