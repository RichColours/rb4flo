package rb.for_flo.domain.nem12

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.format.DateTimeParseException

class ParsingTests {

    @Test
    fun testValidDateParses() {
        val validDate = "250302"

        val ldt = validDate.asNemDate()

        assertThat(ldt.year).isEqualTo(2025)
        assertThat(ldt.monthValue).isEqualTo(3)
        assertThat(ldt.dayOfMonth).isEqualTo(2)
    }

    @Test
    fun testYearTooLongDoesntParse() {
        val invalidDate = "20250302"

        assertThrows<DateTimeParseException> {
            invalidDate.asNemDate()
        }
    }

    @Test
    fun testValidButWithTimeDoesntParse() {
        val invalidDate = "2503020000"

        assertThrows<DateTimeParseException> {
            invalidDate.asNemDate()
        }
    }
}