package dev.toolkt.reactive.cell

import dev.toolkt.core.delegates.weakLazy
import dev.toolkt.reactive.event_stream.EventStream

abstract class LightCell<V>() : ProperCell<V>() {
    final override val newValues: EventStream<V> by weakLazy {
        buildNewValues()
    }

    protected abstract fun buildNewValues(): EventStream<V>
}
