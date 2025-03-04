package rb.for_flow.nem_processing.processing

import java.util.function.Consumer

class CapturingConsumer<T> : Consumer<T> {

    // Private mutable
    private val privateCaptured = mutableListOf<T>()

    // Public readonly
    public val captured: List<T> get() = privateCaptured

    override fun accept(t: T) {
        privateCaptured.add(t)
    }
}
