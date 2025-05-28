package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell_ng.CellNg

class SwitchEventStreamNg<V>(
    private val nestedCell: CellNg<CellNg<V>>,
) : DependentEventStreamNg<V>() {
    override fun observe(): Subscription = object : Subscription {
        private val outerSubscription = nestedCell.newValues.listen { newInnerCell ->
            notify(newInnerCell.currentValue)

            resubscribeToInner(newInnerCell = newInnerCell)
        }

        private var innerSubscription: Subscription = subscribeToInner(
            innerCell = nestedCell.currentValue,
        )

        private fun subscribeToInner(
            innerCell: CellNg<V>,
        ): Subscription = innerCell.newValues.listen { event ->
            notify(event)
        }

        private fun resubscribeToInner(
            newInnerCell: CellNg<V>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerCell = newInnerCell)
        }

        override fun cancel() {
            innerSubscription.cancel()
            outerSubscription.cancel()
        }
    }
}
