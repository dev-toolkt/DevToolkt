package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class HoldCell<V>(
    initialValue: V,
    newValues: EventStream<V>,
) : CachingCell<V>(
    initialValue = initialValue,
    newValues = newValues,
) {
    init {
        init()
    }
}
