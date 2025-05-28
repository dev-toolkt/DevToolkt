package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell_ng.CellNg

class DivertEventStreamNg<E>(
    private val nestedEventStream: CellNg<EventStreamNg<E>>,
) : DependentEventStreamNg<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedEventStream.newValues.listen { newInnerStream ->
            resubscribeToInner(newInnerStream = newInnerStream)
        }

        private var innerSubscription: Subscription = subscribeToInner(
            innerStream = nestedEventStream.currentValue,
        )

        private fun subscribeToInner(
            innerStream: EventStreamNg<E>,
        ): Subscription = innerStream.listen { event ->
            notify(event)
        }

        private fun resubscribeToInner(
            newInnerStream: EventStreamNg<E>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerStream = newInnerStream)
        }

        override fun cancel() {
            innerSubscription.cancel()
            outerSubscription.cancel()
        }
    }
}

