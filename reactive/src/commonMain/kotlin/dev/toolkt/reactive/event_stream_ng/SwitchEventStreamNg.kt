package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.cell_ng.CellNg

class SwitchEventStreamNg<V>(
    nestedCell: CellNg<CellNg<V>>,
) : MultiplexingEventStreamNg<CellNg<V>, V>() {
    override val nestedObject: CellNg<CellNg<V>> = nestedCell

    override fun processNewInnerObject(
        newInnerObject: CellNg<V>,
    ) {
        notify(newInnerObject.currentValue)
    }

    override fun extractInnerStream(
        innerObject: CellNg<V>,
    ): EventStreamNg<V> = innerObject.newValues
}
