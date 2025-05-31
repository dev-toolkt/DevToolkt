package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.SwitchEventStream

class SwitchCell<V>(
    nestedCell: Cell<Cell<V>>,
) : CachingCell<V>(
    initialValue = nestedCell.currentValue.currentValue,
) {
    override val newValues: EventStream<V> = SwitchEventStream(
        nestedCell = nestedCell,
    )

    init {
        init()
    }
}
