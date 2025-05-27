package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.vertices.ManagedVertex

abstract class TransformingEventStreamVertex<E, Er>(
    private val source: ManagedVertex<E>,
) : EventStreamVertex<Er>() {
    protected abstract fun handleSourceEvent(event: E)

    override fun observe(): Subscription = source.subscribeStrong(
        listener = object : Listener<E> {
            override fun handle(event: E) {
                handleSourceEvent(event)
            }
        },
    )
}
