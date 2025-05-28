package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.event_stream_ng.EventEmitterNg
import dev.toolkt.reactive.event_stream_ng.EventStreamNg

class MutableCellNg<V>(
    initialValue: V,
) : ActiveCellNg<V>() {
    private val newValueEmitter = EventEmitterNg<V>()

    private var mutableValue: V = initialValue

    override val newValues: EventStreamNg<V>
        get() = newValueEmitter

    override val currentValue: V
        get() = mutableValue

    fun set(
        newValue: V,
    ) {
        newValueEmitter.emit(newValue)
        mutableValue = newValue
    }
}
