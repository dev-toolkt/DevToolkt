package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.mergeWith

class SwitchCell<V>(
    private val nestedCell: Cell<Cell<V>>,
) : LightCell<V>() {
    override val currentValue: V
        get() = nestedCell.currentValue.currentValue

    override fun buildNewValues(): EventStream<V> = nestedCell.newValues.map { newInnerCell ->
        newInnerCell.currentValue
    }.mergeWith(
        nestedCell.divertOf { it.newValues },
    )
}
