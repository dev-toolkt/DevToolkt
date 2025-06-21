package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell

abstract class MultiplexingEventStream<N, E> : DependentEventStream<E>() {
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
            innerStream: EventStream<E>,
        ): Subscription = innerStream.listen { event ->
            notify(event)
        }

        private fun resubscribeToInner(
            newInnerStream: EventStream<E>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerStream = newInnerStream)
        }

        override fun cancel() {
            innerSubscription.cancel()
            outerSubscription.cancel()
        }
    }

    protected abstract val nestedObject: Cell<N>

    open fun processNewInnerObject(
        newInnerObject: N,
    ) {
    }

    protected abstract fun extractInnerStream(
        innerObject: N,
    ): EventStream<E>
}
