package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

class TakeEventStream<E>(
    source: EventStream<E>,
    count: Int,
) : StatefulEventStream<E>() {
    init {
        require(count > 0)
    }

    private var remainingCount: Int = count

    private var sourceSubscription: Subscription? = source.listenWeak(
        target = this,
    ) { self, sourceEvent ->
        val remainingCount = self.remainingCount

        if (remainingCount <= 0) {
            throw IllegalStateException("No more remaining events to take")
        }

        val newRemainingCount = remainingCount - 1
        self.remainingCount = newRemainingCount

        notify(event = sourceEvent)

        if (newRemainingCount == 0) {
            val sourceSubscription =
                this.sourceSubscription ?: throw IllegalStateException("No active source subscription")

            sourceSubscription.cancel()

            this.sourceSubscription = null
        }
    }
}
