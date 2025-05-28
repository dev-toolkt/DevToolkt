package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

object NeverEventStream : EventStream<Nothing>() {
    override fun <Er> map(
        transform: (Nothing) -> Er,
    ): EventStream<Er> = NeverEventStream

    override fun filter(
        predicate: (Nothing) -> Boolean,
    ): EventStream<Nothing> = NeverEventStream

    override fun <T : Any> pipe(
        target: T,
        forward: (T, Nothing) -> Unit,
    ): Subscription = Subscription.Noop

    override fun listen(
        listener: Listener<Nothing>,
    ): Subscription = Subscription.Noop

    override fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, Nothing>
    ): Subscription = Subscription.Noop
}
