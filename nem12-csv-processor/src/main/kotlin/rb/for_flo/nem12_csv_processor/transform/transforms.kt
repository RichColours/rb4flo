package rb.for_flo.nem12_csv_processor.transform

import rb.for_flo.domain.consumption.ConsumptionRecord
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME

fun LocalDateTime.asPostgresTimestamp(): String = this.format(ISO_LOCAL_DATE_TIME)

fun toPostgresConsumptionInsertStatement(consumptionStatement: ConsumptionRecord): String {
    with(consumptionStatement) {
        return """
            insert into meter_readings(
                nmi, timestamp, consumption
            ) values (
                '$nmi', '${timestamp.asPostgresTimestamp()}', $consumption
            );
        """.trimIndent()
    }
}
