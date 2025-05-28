package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription

abstract class DependentEventStream<E> : ManagedEventStream<E>() {
    private var subscription: Subscription? = null

    final override fun onResumed() {
        if (subscription != null) {
            throw AssertionError("The subscription is already present")
        }

        subscription = observe()
    }

    final override fun onPaused() {
        val subscription = this.subscription ?: throw AssertionError("There's no subscription")

        subscription.cancel()
        this@DependentEventStream.subscription = null
    }

    protected abstract fun observe(): Subscription
}
