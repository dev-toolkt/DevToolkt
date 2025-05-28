package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.event_stream_ng.EventStreamNg
import dev.toolkt.reactive.vertices.event_stream.MapEventStreamVertex

class MapCellNg<V, Vr>(
    source: CellNg<V>,
    transform: (V) -> Vr,
) : DependentCellNg<Vr>(
    initialValue = transform(source.currentValue),
) {
    override val newValues: EventStreamNg<Vr> = source.newValues.map(transform)
}
