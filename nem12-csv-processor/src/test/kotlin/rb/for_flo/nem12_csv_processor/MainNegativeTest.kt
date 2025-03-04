package rb.for_flo.nem12_csv_processor

import assertk.assertThat
import assertk.assertions.isEqualTo
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.test.Test
import kotlin.test.assertFailsWith

class MainNegativeTest {

    @Test
    fun testMainNegativeWithSomeMalformedData() {

        val inputCsv = Path("src/main/resources/mainTests/negative01/nem.txt")
        val outputInserts = Files.createTempFile("rb.for-flo.nem12-csv-processor.inserts_", "")

        assertFailsWith<Exception> {
            Main.main(
                arrayOf(
                    inputCsv.absolutePathString(),
                    outputInserts.absolutePathString()
                )
            )
        }
            .also {
                assertThat(it.message).isEqualTo("Error processing CSV line number 0")
            }
    }
}
