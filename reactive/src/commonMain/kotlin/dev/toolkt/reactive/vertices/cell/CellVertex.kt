package dev.toolkt.reactive.vertices.cell

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.vertices.ManagedVertex

abstract class CellVertex<V>(
    initialValue: V,
) : ManagedVertex<Cell.Change<V>>() {
    private var mutableValue: V = initialValue

    val currentValue: V
        get() = mutableValue

    protected fun update(newValue: V) {
        val oldValue = mutableValue

        mutableValue = newValue

        notify(
            Cell.Change(
                oldValue = oldValue,
                newValue = newValue,
            )
        )
    }
}
