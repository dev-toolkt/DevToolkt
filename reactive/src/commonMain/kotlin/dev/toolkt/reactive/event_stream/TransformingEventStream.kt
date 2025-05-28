package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class TransformingEventStream<E, Er>(
    private val source: EventStream<E>,
) : DependentEventStream<Er>() {
    final override fun observe(): Subscription = source.listen { event ->
        transformEvent(event = event)
    }

    protected abstract fun transformEvent(
        event: E,
    )
}
