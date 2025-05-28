package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.ActiveEventStream
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.NeverEventStream
import dev.toolkt.reactive.vertices.cell.CellVertex

internal class DivertEventStreamVertex<E>(
    private val nestedEventStream: CellVertex<EventStream<E>>,
) : EventStreamVertex<E>() {
    override val kind: String = "Divert"

    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedEventStream.subscribeStrongRaw {
            resubscribeToInner(newInnerStream = it.newValue)
        }

        private var innerSubscription: Subscription = subscribeToInner(
            nestedEventStream.currentValue,
        )

        private fun subscribeToInner(
            innerStream: EventStream<E>,
        ): Subscription = when (innerStream) {
            is ActiveEventStream<E> -> innerStream.vertex.subscribeStrongRaw {
                notify(it)
            }

            NeverEventStream -> Subscription.Noop
        }

        private fun resubscribeToInner(
            newInnerStream: EventStream<E>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerStream = newInnerStream)
        }

        override fun cancel() {
            outerSubscription.cancel()
            innerSubscription.cancel()
        }
    }
}
