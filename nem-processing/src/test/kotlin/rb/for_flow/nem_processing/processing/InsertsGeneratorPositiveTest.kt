package rb.for_flow.nem_processing.processing

import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import rb.for_flo.domain.consumption.ConsumptionRecord
import java.math.BigDecimal

class InsertsGeneratorPositiveTest {

    @Test
    fun testSimpleOne200One300With30Interval() {

        val input = """
            100,NEM12,200301011534,MDP1,Retailer1
            200,NMI_1,E1Q1,Register_1,suffix,id,meterSn_1,kWh,30,20040120
            300,250101,1.3,2.3,3.3,4.3,5.3,6.3,7.3,8.3,9.3,10.3,11.3,12.3,13.3,14.3,15.3,16.3,17.3,18.3,19.3,20.3,21.3,22.3,23.3,24.3,25.3,26.3,27.3,28.3,29.3,30.3,31.3,32.3,33.3,34.3,35.3,36.3,37.3,38.3,39.3,40.3,41.3,42.3,43.3,44.3,45.3,46.3,47.3,48.3,V,,,20030101153445,20030102023012
            900
        """.trimIndent().lines().asSequence()

        val captures = CapturingConsumer<ConsumptionRecord>()

        InsertStreamGenerator().generate(input, captures)

        assertThat(captures.captured).hasSize(48)

        assertThat(captures.captured.map { it.consumption }.reduce(BigDecimal::add).toFloat())
            .isEqualTo(48 * 0.3f + (1..48).sum())
    }
}
