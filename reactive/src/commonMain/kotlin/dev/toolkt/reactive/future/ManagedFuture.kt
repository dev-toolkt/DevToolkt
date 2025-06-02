package dev.toolkt.reactive.future

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

abstract class ManagedFuture<out V> : ProperFuture<V>() {
    private var internalState: State<V> = Pending

    private val onResultEmitter = EventEmitter<Fulfilled<V>>()

    final override val currentState: State<V>
        get() = internalState

    final override val onFulfilled: EventStream<Fulfilled<V>>
        get() = onResultEmitter

    protected fun completeInternally(
        result: @UnsafeVariance V,
    ) {
        if (internalState is Fulfilled<*>) {
            throw IllegalStateException("The future is already fulfilled")
        }

        val fulfilledState = Fulfilled(result = result)

        onResultEmitter.emit(fulfilledState)

        internalState = fulfilledState
    }
}
