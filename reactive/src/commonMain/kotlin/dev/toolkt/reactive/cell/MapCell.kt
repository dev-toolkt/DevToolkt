package dev.toolkt.reactive.cell

class MapCell<V, Vr>(
    source: Cell<V>,
    transform: (V) -> Vr,
) : CachingCell<Vr>(
    initialValue = transform(source.currentValue),
    newValues = source.newValues.map(transform),
) {
    init {
        init()
    }
}
