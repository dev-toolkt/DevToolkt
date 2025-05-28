package dev.toolkt.reactive.vertices.cell

internal class MapCellVertex<V, Vr>(
    private val source: CellVertex<V>,
    private val transform: (V) -> Vr,
) : DependentCellVertex<Vr>(
    initialValue = transform(source.currentValue),
) {
    override val kind: String = "MapC"

    override fun buildHybridSubscription() = source.subscribeHybridRaw {
        update(transform(it.newValue))
    }


    init {
        init()
    }
}
