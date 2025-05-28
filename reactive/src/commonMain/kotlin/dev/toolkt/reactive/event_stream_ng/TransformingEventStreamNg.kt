package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription

abstract class TransformingEventStreamNg<E, Er>(
    private val source: EventStreamNg<E>,
) : DependentEventStreamNg<Er>() {
    final override fun observe(): Subscription = source.listen { event ->
        transformEvent(event = event)
    }

    protected abstract fun transformEvent(
        event: E,
    )
}
