package dev.toolkt.reactive.vertices.event_stream

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.vertices.ManagedVertex

abstract class EventStreamVertex<E> : ManagedVertex<E>() {
    private var subscription: Subscription? = null

    final override fun onResumed() {
        if (subscription != null) {
            throw AssertionError("The stream $tag is already resumed (???)")
        }

        subscription = observe()
    }

    final override fun onPaused() {
        val sourceSubscription =
            this.subscription ?: throw AssertionError("The stream $tag is already paused (???)")

        sourceSubscription.cancel()
        subscription = null
    }

    protected abstract fun observe(): Subscription
}
