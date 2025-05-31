package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class HoldCell<V>(
    override val newValues: EventStream<V>,
    initialValue: V,
) : CachingCell<V>(
    initialValue = initialValue,
) {
    init {
        init()
    }
}
