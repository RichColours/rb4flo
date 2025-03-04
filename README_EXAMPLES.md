# Some example outputs

## Showing a line number error exception

    > Task :nem12-csv-processor:run FAILED
    NEM12 CSV Processor - generate insert statements
    ================================================
    rb.for_flow.nem_processing.processing.LineNumberException: Error processing CSV line number 2
    at rb.for_flow.nem_processing.processing.InsertStreamGeneratorKt.withLineNumberException(InsertStreamGenerator.kt:105)
    at rb.for_flow.nem_processing.processing.InsertStreamGenerator.generate(InsertStreamGenerator.kt:32)
    at rb.for_flo.nem12_csv_processor.Main$Companion.main(Main.kt:42)
    at rb.for_flo.nem12_csv_processor.Main.main(Main.kt)
    Caused by: java.lang.Exception: Invalid intervalLength 20
    at rb.for_flo.domain.nem12.RecordTypesKt.record200-eFu7Lso(recordTypes.kt:43)

## How to run createNem12.sh

    ./createNem12.sh 1 1 30 1> nem.txt

This runs the tool to create one NMI for one day with 30 minute intervalLength.

Redirect STDOUT (only) to nem.txt.

The tool produces STDERR for debugging so don't consume that!

## Running the nem12-csv-processor

    ./gradlew :nem12-csv-processor:run --args '/<snip>/nem.txt /<snip>/inserts.txt'

Run the right project with Gradle, and `--args` to pass the input and output filenames to the tool.
