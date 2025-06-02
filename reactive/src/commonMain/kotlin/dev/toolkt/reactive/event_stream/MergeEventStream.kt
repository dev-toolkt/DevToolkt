package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

class MergeEventStream<E>(
    private val source1: EventStream<E>,
    private val source2: EventStream<E>,
) : DependentEventStream<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val subscription1 = source1.listen { event ->
            notify(event = event)
        }

        private val subscription2 = source2.listen { event ->
            notify(event = event)
        }

        override fun cancel() {
            subscription1.cancel()
            subscription2.cancel()
        }
    }
}
