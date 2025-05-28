package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.cell.Cell

class DivertEventStream<V>(
    nestedEventStream: Cell<EventStream<V>>,
) : MultiplexingEventStream<EventStream<V>, V>() {
    override val nestedObject: Cell<EventStream<V>> = nestedEventStream

    override fun extractInnerStream(
        innerObject: EventStream<V>,
    ): EventStream<V> = innerObject
}
