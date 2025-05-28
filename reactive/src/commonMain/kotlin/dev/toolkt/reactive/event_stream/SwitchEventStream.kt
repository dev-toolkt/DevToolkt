package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell

class SwitchEventStream<V>(
    nestedCell: Cell<Cell<V>>,
) : MultiplexingEventStream<Cell<V>, V>() {
    override val nestedObject: Cell<Cell<V>> = nestedCell

    override fun processNewInnerObject(
        newInnerObject: Cell<V>,
    ) {
        notify(newInnerObject.currentValue)
    }

    override fun extractInnerStream(
        innerObject: Cell<V>,
    ): EventStream<V> = innerObject.newValues
}
