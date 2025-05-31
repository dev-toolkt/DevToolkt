package dev.toolkt.reactive.cell

import dev.toolkt.reactive.event_stream.EventStream

abstract class CachingCell<V>(
    initialValue: V,
    final override val newValues: EventStream<V>,
) : ProperCell<V>() {
    internal var cachedValue: V = initialValue

    override val currentValue: V
        get() = cachedValue

    protected fun init() {
        newValues.listenWeak(
            target = this,
        ) { self, newValue ->
            self.cachedValue = newValue
        }
    }
}
