package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

interface EventSourceNg<out E> {
    fun listen(
        listener: Listener<E>,
    ): Subscription

    fun <T: Any> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription
}
