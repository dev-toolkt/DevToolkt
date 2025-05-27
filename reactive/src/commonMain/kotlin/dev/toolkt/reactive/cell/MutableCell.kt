package dev.toolkt.reactive.cell

import dev.toolkt.reactive.vertices.cell.MutableCellVertex

class MutableCell<V>(
    initialValue: V,
) : ActiveCell<V>() {
    override val vertex = MutableCellVertex(
        initialValue = initialValue,
    )

    fun set(newValue: V) {
        vertex.set(newValue)
    }
}
