import function.MainFunction
import function.MathFunction
import trig.Cos
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.math.BigDecimal

class СsvFileWriter {
    private val writer: BufferedWriter

    constructor(outputDir: File) {
        writer = BufferedWriter(FileWriter(outputDir))
    }

    fun write(
        startX: BigDecimal,
        endX: BigDecimal,
        step: BigDecimal,
        function: MathFunction,
        accuracy: BigDecimal
    ) {
        try{
            writer.write("x, y")
            writer.newLine()

            var currentX = startX
            while(currentX < endX) {
                try{
                    val y = function.calculate(currentX, accuracy)
                    writer.write("${currentX.toPlainString()}, ${y.toPlainString()}")
                }
                catch (_: IllegalArgumentException) {
                    writer.write("${currentX.toPlainString()}, Nan")
                }
                writer.newLine()

                currentX += step
            }
        }
        finally{
            writer.flush()
        }
    }
}