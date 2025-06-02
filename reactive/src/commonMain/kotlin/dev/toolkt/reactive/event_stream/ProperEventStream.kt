package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class ProperEventStream<out E> : EventStream<E>() {
    final override fun <Er> map(
        transform: (E) -> Er,
    ): EventStream<Er> = MapEventStream(
        source = this,
        transform = transform,
    )

    final override fun filter(
        predicate: (E) -> Boolean,
    ): EventStream<E> = FilterEventStream(
        source = this,
        predicate = predicate,
    )

    override fun take(
        count: Int,
    ): EventStream<E> {
        require(count >= 0)

        return when (count) {
            0 -> NeverEventStream

            else -> TakeEventStream(
                source = this,
                count = count,
            )
        }
    }

    final override fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription = listenWeak(
        target = target,
        listener = forward,
    )
}
