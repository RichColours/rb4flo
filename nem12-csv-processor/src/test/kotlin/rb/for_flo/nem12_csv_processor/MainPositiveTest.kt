package rb.for_flo.nem12_csv_processor

import assertk.assertThat
import assertk.assertions.isEqualTo
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.test.Test

class MainPositiveTest {

    @Test
    fun testMainPositiveWithPreBuildData() {

        val inputCsv = Path("src/main/resources/mainTests/positive01/nem.txt")
        val outputInserts = Files.createTempFile("rb.for-flo.nem12-csv-processor.inserts_", "")
        Main.main(
            arrayOf(
                inputCsv.absolutePathString(),
                outputInserts.absolutePathString()
            )
        )

        val entireOutput = Files.readAllLines(outputInserts)
        val numInserts = 2 * 31 * 24 * (60 / 5)

        assertThat(entireOutput.count { it.contains("insert into") }).isEqualTo(numInserts)
    }
}
