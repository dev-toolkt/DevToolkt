package dev.toolkt.reactive.reactive_list

import dev.toolkt.reactive.cell.ActiveCell
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.LoopedEventStream

class LoopedCell<V>(
    private val placeholderValue: V,
) : ActiveCell<V>() {
    private var loopedCell: Cell<V>? = null

    private val newValuesLooped = LoopedEventStream<V>()

    override val newValues: EventStream<V>
        get() = newValuesLooped

    override val currentValue: V
        get() = this.loopedCell?.currentValue ?: placeholderValue

    fun loop(
        cell: Cell<V>,
    ) {
        if (loopedCell != null) {
            throw IllegalStateException("The reactive list is already looped")
        }

        loopedCell = cell

        newValuesLooped.loop(
            eventStream = cell.newValues,
            initialEvent = cell.currentValue,
        )
    }
}
