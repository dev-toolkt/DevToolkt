package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Subscription

abstract class DependentEventStreamNg<E> : ManagedEventStreamNg<E>() {
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
        this@DependentEventStreamNg.subscription = null
    }

    protected abstract fun observe(): Subscription
}
