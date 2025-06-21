package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.future.ManagedFuture

class NextFuture<E>(
    source: EventStream<E>,
) : ManagedFuture<E>() {
    private var sourceSubscription: Subscription? = source.listenWeak(
        target = this,
    ) { self, sourceEvent ->
        self.completeInternally(
            result = sourceEvent,
        )

        val sourceSubscription = this.sourceSubscription ?: throw IllegalStateException("No active source subscription")

        sourceSubscription.cancel()

        this.sourceSubscription = null
    }
}
