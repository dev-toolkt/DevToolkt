package dev.toolkt.reactive.vertices.cell

class MutableCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue,
) {
    override val kind: String = "MutableC"

    override fun onResumed() {
    }

    override fun onPaused() {
    }

    fun set(newValue: V) {
        update(newValue)
    }
}
