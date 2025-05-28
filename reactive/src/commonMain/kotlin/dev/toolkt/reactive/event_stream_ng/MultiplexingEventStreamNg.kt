package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell_ng.CellNg

abstract class MultiplexingEventStreamNg<N, E> : DependentEventStreamNg<E>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedObject.newValues.listen { newInnerObject ->
            processNewInnerObject(
                newInnerObject = newInnerObject,
            )

            resubscribeToInner(
                newInnerStream = extractInnerStream(newInnerObject),
            )
        }

        private var innerSubscription: Subscription = subscribeToInner(
            innerStream = extractInnerStream(nestedObject.currentValue),
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

    protected abstract val nestedObject: CellNg<N>

    open fun processNewInnerObject(
        newInnerObject: N,
    ) {
    }

    protected abstract fun extractInnerStream(
        innerObject: N,
    ): EventStreamNg<E>
}
