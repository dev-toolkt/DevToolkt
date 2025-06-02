package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.HoldCell

typealias WeakListener<T, E> = (T, E) -> Unit

abstract class EventStream<out E> : EventSource<E> {
    companion object {
        val Never: EventStream<Nothing> = NeverEventStream

        fun <E, R> looped(
            block: (EventStream<E>) -> Pair<R, EventStream<E>>,
        ): R {
            val loopedEventStream = LoopedEventStream<E>()

            val (result, eventStream) = block(loopedEventStream)

            loopedEventStream.loop(eventStream)

            return result
        }

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

    abstract fun take(
        count: Int,
    ): EventStream<E>

    abstract fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription

    abstract fun <E> mergeWith(
        other: EventStream<E>,
    ): EventStream<E>

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
    initialValue = initialValue,
    newValues = this,
)
