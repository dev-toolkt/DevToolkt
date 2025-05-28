package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell_ng.CellNg
import dev.toolkt.reactive.cell_ng.HoldCellNg

typealias WeakListener<T, E> = (T, E) -> Unit

abstract class EventStreamNg<out E> : EventSourceNg<E> {
    companion object {
        val Never: EventStreamNg<Nothing> = NeverEventStreamNg

        fun <V> divert(
            nestedEventStream: CellNg<EventStreamNg<V>>,
        ): EventStreamNg<V> = DivertEventStreamNg(
            nestedEventStream = nestedEventStream,
        )
    }

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): EventStreamNg<Er>

    abstract fun filter(
        predicate: (E) -> Boolean,
    ): EventStreamNg<E>

    abstract fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription

    fun <T : Any> pipeAndForget(
        target: T,
        forward: (T, E) -> Unit,
    ) {
        // Forget the subscription, relying purely on garbage collection
        pipe(
            target = target,
            forward = forward,
        )
    }
}

fun <E> EventStreamNg<E>.hold(
    initialValue: E,
): CellNg<E> = HoldCellNg(
    newValues = this,
    initialValue = initialValue,
)
