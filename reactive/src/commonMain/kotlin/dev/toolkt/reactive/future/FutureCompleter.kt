package dev.toolkt.reactive.future

import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream

class FutureCompleter<V> : ProperFuture<V>() {
    private var mutableState: State<V> = Pending

    private val onFulfilledEmitter = EventEmitter<Fulfilled<V>>()

    override val currentState: State<V>
        get() = mutableState

    override val onFulfilled: EventStream<Fulfilled<V>>
        get() = onFulfilledEmitter

    fun complete(
        result: V,
    ) {
        if (mutableState is Fulfilled<*>) {
            throw IllegalStateException("The future is already fulfilled")
        }

        onFulfilledEmitter.emit(
            Fulfilled(result),
        )

        mutableState = Fulfilled(result)
    }
}
