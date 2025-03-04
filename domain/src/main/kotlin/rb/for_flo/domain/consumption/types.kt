package rb.for_flo.domain.consumption

import java.math.BigDecimal
import java.time.LocalDateTime

data class ConsumptionRecord(
    val nmi: String,
    val timestamp: LocalDateTime,
    val consumption: BigDecimal
)
