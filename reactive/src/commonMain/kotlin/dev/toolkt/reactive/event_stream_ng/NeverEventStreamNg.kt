package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

object NeverEventStreamNg : EventStreamNg<Nothing>() {
    override fun <Er> map(
        transform: (Nothing) -> Er,
    ): EventStreamNg<Er> = NeverEventStreamNg

    override fun filter(
        predicate: (Nothing) -> Boolean,
    ): EventStreamNg<Nothing> = NeverEventStreamNg

    override fun <T : Any> pipe(
        target: T,
        forward: (T, Nothing) -> Unit,
    ): Subscription = Subscription.Noop

    override fun listen(
        listener: Listener<Nothing>,
    ): Subscription = Subscription.Noop

    override fun <T> listenWeak(
        target: T,
        listener: WeakListener<T, Nothing>,
    ): Subscription = Subscription.Noop
}
