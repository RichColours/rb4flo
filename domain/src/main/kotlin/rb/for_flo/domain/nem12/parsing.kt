package rb.for_flo.domain.nem12

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.asNemDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ofPattern("uuMMdd"))

fun String.asNemDateTime12(): LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ofPattern("uuuuMMddHHmm"))
