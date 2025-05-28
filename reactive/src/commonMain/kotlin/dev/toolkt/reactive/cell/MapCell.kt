package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

class MapCell<V, Vr>(
    source: Cell<V>,
    transform: (V) -> Vr,
) : DependentCell<Vr>(
    initialValue = transform(source.currentValue),
) {
    override val newValues: EventStream<Vr> = source.newValues.map(transform)

    init {
        init()
    }
}
