package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription

abstract class ActiveEventStreamNg<out E> : EventStreamNg<E>() {
    final override fun <Er> map(
        transform: (E) -> Er,
    ): EventStreamNg<Er> = MapEventStreamNg(
        source = this,
        transform = transform,
    )

    final override fun filter(
        predicate: (E) -> Boolean,
    ): EventStreamNg<E> = FilterEventStreamNg(
        source = this,
        predicate = predicate,
    )

    final override fun <T : Any> pipe(
        target: T,
        forward: (T, E) -> Unit,
    ): Subscription = listenWeak(
        target = target,
        listener = forward,
    )
}
