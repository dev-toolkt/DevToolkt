package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.cell_ng.CellNg

class DivertEventStreamNg<V>(
    nestedEventStream: CellNg<EventStreamNg<V>>,
) : MultiplexingEventStreamNg<EventStreamNg<V>, V>() {
    override val nestedObject: CellNg<EventStreamNg<V>> = nestedEventStream

    override fun extractInnerStream(
        innerObject: EventStreamNg<V>,
    ): EventStreamNg<V> = innerObject
}
