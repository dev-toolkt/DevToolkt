package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

internal object NeverEventStream : EventStream<Nothing>() {
    override fun <Er> map(
        transform: (Nothing) -> Er,
    ): EventStream<Er> = NeverEventStream

    override fun filter(
        predicate: (Nothing) -> Boolean,
    ): EventStream<Nothing> = NeverEventStream

    override fun <T : Any> pipe(
        target: T,
        consume: (Nothing) -> Unit,
    ): Subscription.Noop = Subscription.Noop
}
