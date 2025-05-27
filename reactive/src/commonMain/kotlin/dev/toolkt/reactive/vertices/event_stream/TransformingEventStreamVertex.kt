package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.vertices.Vertex

abstract class TransformingEventStreamVertex<E, Er>(
    private val source: Vertex<E>,
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
