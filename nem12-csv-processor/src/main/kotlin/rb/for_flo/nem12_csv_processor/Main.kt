package rb.for_flo.nem12_csv_processor

import rb.for_flo.domain.consumption.ConsumptionRecord
import rb.for_flo.nem12_csv_processor.transform.toPostgresConsumptionInsertStatement
import rb.for_flow.nem_processing.processing.InsertStreamGenerator
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.function.Consumer
import kotlin.io.path.Path
import kotlin.streams.asSequence

class Main {

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            println("NEM12 CSV Processor - generate insert statements")
            println("================================================")


            val lineSeq = Files.lines(Path(args[0])).asSequence()

            val outputFileWriter = Files.newBufferedWriter(
                Path(args[1]),
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE
            )

            val lineWritingConsumer = Consumer<ConsumptionRecord> {
                outputFileWriter.write(
                    toPostgresConsumptionInsertStatement(it) + System.lineSeparator()
                )
            }

            val insertGenerator = InsertStreamGenerator()

            outputFileWriter.use {
                insertGenerator.generate(lineSeq, lineWritingConsumer)
            }

        }
    }
}
