package dev.toolkt.reactive.vertices.cell

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.strengthen
import dev.toolkt.reactive.weaken

/**
 * A vertex of a cell that depends on another vertices (cells and/or streams),
 * installing a hybrid (weak/strong) subscription to the dependencies.
 */
// Another strategy for implementing this class could be separating the state
// and change subscriptions. We'd install (up to) two subscriptions to the
// dependencies. One, the state subscription, would be always weak and would
// be potentially garbage collected soon after the cell is created, assuming
// the direct strong reference to the cell is discarded by the user. The cell
// might still be used purely by its change stream, though. The subscription
// management would be the one used for "normal" event streams - the strong
// subscription could come and go as the change vertex pauses/unpauses. We'd need
// to  ensure that the `changes` / `newValues` event streams don't hold a
// reference to the cell's state vertex. This might require having two vertices
// for each cell.
abstract class DependentCellVertex<V>(
    initialValue: V,
) : CellVertex<V>(
    initialValue = initialValue,
) {
    lateinit var subscription: HybridSubscription

    final override fun onResumed() {
        // This cell's changes now influence the state of the world by side effects,
        // even when in a case when no actual strong reference _to_ this cell is
        // being held. We must strengthen our subscription, so the dependencies
        // keeps this cell alive and the event propagation chain active.

        subscription.strengthen()
    }

    final override fun onPaused() {
        // Although this cell stopped being actively observed, it may still be
        // sampled at a later point. We switch back to a weak subscription to
        // kep the state up-to-date.

        subscription.weaken()
    }

    protected fun init() {
        // We create an initial (weak) subscription, so the state of this cell
        // is kept up-to-date until the first listener subscribes, if it ever
        // happens. The only purpose of this cell might be being sampled.
        // If no one subscribes to this cell nor will it be kept for sampling
        // purposes, it will be garbage-collected (which is possible, as the
        // dependencies' references are weak).

        subscription = buildHybridSubscription()
    }

    /**
     * Subscribe to the dependencies and return a hybrid subscription.
     */
    protected abstract fun buildHybridSubscription(): HybridSubscription
}
