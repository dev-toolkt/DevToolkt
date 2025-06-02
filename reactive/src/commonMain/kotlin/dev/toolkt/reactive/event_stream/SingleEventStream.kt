package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

class SingleEventStream<E>(
    source: EventStream<E>,
) : StatefulEventStream<E>() {
    private var wasEmitted = false

    private var sourceSubscription: Subscription? = source.listenWeak(
        target = this,
    ) { self, sourceEvent ->
        if (wasEmitted) {
            throw IllegalStateException("The single event was already emitted")
        }

        notify(event = sourceEvent)

        wasEmitted = true

        val sourceSubscription = this.sourceSubscription ?: throw IllegalStateException("No active source subscription")

        sourceSubscription.cancel()

        this.sourceSubscription = null
    }
}
