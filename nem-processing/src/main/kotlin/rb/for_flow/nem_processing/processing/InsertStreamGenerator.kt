package rb.for_flow.nem_processing.processing

import rb.for_flo.domain.consumption.ConsumptionRecord
import rb.for_flo.domain.csv.CsvLine
import rb.for_flo.domain.nem12.Record200
import rb.for_flo.domain.nem12.record100
import rb.for_flo.domain.nem12.record200
import rb.for_flo.domain.nem12.record300
import java.util.function.Consumer

/**
 * Validates as it goes, generating insert statements from the consumption in the 300 records.
 *
 * Defined to take a Sequence because that's a reasonable 'lazy reading' type.
 *
 * Mostly built in a forEach loop because the 200-300 relationship is stateful and I cannot find a clean
 * way to operate on this in a Sequence functional way.
 *
 * Data is returned through a Consumer because that is most flexible, doesn't lock me into returning a Sequence.
 */
class InsertStreamGenerator {

    fun generate(csvSequence: Sequence<String>, lineWritingConsumer: Consumer<ConsumptionRecord>) {

        var counted100 = 0
        var holdRecord200: Record200? = null
        var seen900Last = false

        csvSequence
            .map { CsvLine(it.split(',')) }
            .forEachIndexed { index, it ->

                withLineNumberException(index) {

                    seen900Last = false

                    when (val recordType = it.fields[0].toInt()) {

                        100 -> {
                            counted100 += 1
                            record100(it) // Create the object to confirm parsing but we don't need to hold it
                            holdRecord200 = null
                        }

                        200 -> {
                            // NotImplemented: check  that we've not seen adjacent 200 records
                            holdRecord200 = record200(it)
                        }

                        300 -> {
                            if (holdRecord200 == null)
                                throw Exception("300 record observed but no corresponding 200 record")

                            process200And300(lineWritingConsumer, holdRecord200!!, it)
                        }

                        400 -> {
                            // Ignore
                            holdRecord200 = null
                        }

                        500 -> {
                            // Ignore
                            holdRecord200 = null
                        }

                        900 -> {
                            seen900Last = true
                            holdRecord200 = null
                        }

                        else -> throw Exception("Unknown record type $recordType")
                    }
                }
            }

        if (counted100 != 1) throw Exception("Counted 100 was not 1, was $counted100")
        if (!seen900Last) throw Exception("900 was not the last record")
    }

    private fun process200And300(consumer: Consumer<ConsumptionRecord>, record200: Record200, record300Csv: CsvLine) {

        val record300 = record300(record300Csv, record200.intervalLength)

        val nmi = record200.nmi

        record300.intervalValues.forEachIndexed { index, consumptionValue ->

            val timestampBeginning = record300.intervalDate.atStartOfDay()
            val intervalMinutes = record200.intervalLength
            val timestamp = timestampBeginning.plusMinutes(intervalMinutes * index.toLong())

            val statement = ConsumptionRecord(nmi, timestamp, consumptionValue)
            consumer.accept(statement)
        }
    }
}

class LineNumberException(message: String, cause: Exception, val lineNumber: Int) : Exception(message, cause)

/**
 * Nice way to wrap the per-line work in a way to convey the line number context with the original exception.
 */
fun <T> withLineNumberException(index: Int, f: () -> T): T {
    try {
        return f()
    } catch (e: Exception) {
        throw LineNumberException("Error processing CSV line number $index", e, index)
    }
}
