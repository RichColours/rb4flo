package rb.for_flo.domain.nem12

import rb.for_flo.domain.csv.CsvLine
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class Record100(
    val version: String,
    val dateTime: LocalDateTime,
    val fromParticipant: String,
    val toParticipant: String
)

data class Record200(
    val nmi: String,
    val intervalLength: Int
)

data class Record300(
    val intervalDate: LocalDate,
    val intervalValues: List<BigDecimal>
)

fun CsvLine.assertRecordIndicatorAndFields(indicator: String, numFields: Int) {
    if (this.fields.size != numFields) throw Exception("Expecting $numFields fields but has ${this.fields.size}")
    if (this.fields[0] != indicator) throw Exception("Expecting indicator to be $indicator but is ${this.fields[0]}")
}

fun record100(csv: CsvLine): Record100 {
    csv.assertRecordIndicatorAndFields("100", 5)

    if (csv.fields[1] != "NEM12") throw Exception("Expecting versionHeader to be NEM12 but was ${csv.fields[1]}")

    return Record100(csv.fields[1], csv.fields[2].asNemDateTime12(), csv.fields[3], csv.fields[4])
}

fun record200(csv: CsvLine): Record200 {
    csv.assertRecordIndicatorAndFields("200", 10)

    val intervalLength = csv.fields[8].toInt()

    if (intervalLength !in setOf(5, 15, 30)) throw Exception("Invalid intervalLength $intervalLength")

    return Record200(csv.fields[1], intervalLength)
}

fun record300(csv: CsvLine, forIntervalLength: Int): Record300 {

    val numExtraFields = 7 // Number of fields in a 300 other than the interval values
    val expectedIntervalValues = 1440 / forIntervalLength

    csv.assertRecordIndicatorAndFields("300", expectedIntervalValues + numExtraFields)

    val intervalDate = csv.fields[1].asNemDate()
    val intervalValues = csv.fields.subList(2, 2 + expectedIntervalValues).map { it.toBigDecimal() }

    return Record300(intervalDate, intervalValues)
}
