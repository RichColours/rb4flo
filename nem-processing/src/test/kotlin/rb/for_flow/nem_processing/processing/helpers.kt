package rb.for_flow.nem_processing.processing

import rb.for_flo.domain.consumption.ConsumptionRecord

/**
 * Util to reduce code duplication.
 */
fun runGenerateAndReturnCapturing(input: Sequence<String>): CapturingConsumer<ConsumptionRecord> {

    val capturing = CapturingConsumer<ConsumptionRecord>()
    InsertStreamGenerator().generate(input, capturing)
    return capturing
}
