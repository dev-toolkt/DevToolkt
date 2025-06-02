package dev.toolkt.reactive.future

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold

abstract class ProperFuture<out V> : Future<V>() {
    final override val state: Cell<State<V>>
        get() = when (val foundState = currentState) {
            is Fulfilled<V> -> Cell.Companion.of(foundState)

            Pending -> onFulfilled.hold(Pending)
        }

    override val onResult: EventStream<V>
        get() = onFulfilled.map { it.result }

    override val onFulfilled: EventStream<Fulfilled<V>>
        get() = onResult.map { result ->
            Fulfilled(result = result)
        }

    final override fun <Vr> map(
        transform: (V) -> Vr,
    ): Future<Vr> = when (val foundState = currentState) {
        is Fulfilled<V> -> of(
            constResult = transform(foundState.result),
        )

        Pending -> onResult.map(transform).next()
    }
}
