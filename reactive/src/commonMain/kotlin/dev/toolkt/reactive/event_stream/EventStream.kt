package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.HoldCell

typealias WeakListener<T, E> = (T, E) -> Unit

abstract class EventStream<out E> : EventSourceNg<E> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

        fun <V> divert(
            nestedEventStream: Cell<EventStream<V>>,
        ): EventStream<V> = DivertEventStream(
            nestedEventStream = nestedEventStream,
        )
    }

    abstract fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er>

    abstract fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E>

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

    fun units(): EventStream<Unit> = map { }
}

fun <E> EventStream<*>.cast(): EventStream<E> {
    @Suppress("UNCHECKED_CAST") return this as EventStream<E>
}

fun <E> EventStream<E>.hold(
    initialValue: E,
): Cell<E> = HoldCell(
    newValues = this,
    initialValue = initialValue,
)
