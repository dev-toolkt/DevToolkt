package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

abstract class ManagedEventStreamNg<E> : ActiveEventStreamNg<E>() {
    private val listeners = mutableSetOf<Listener<E>>()

    final override fun listen(
        listener: Listener<E>,
    ): Subscription {
        val wasAdded = listeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already registered")

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = listeners.add(listener)

                if (!wasRemoved) throw AssertionError("Listener wasn't registered")
            }
        }
    }

    final override fun <T> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription {
        TODO("Not yet implemented")
    }

    protected fun notify(
        event: E,
    ) {
        listeners.forEach {
            it(event)
        }

        // TODO: Notify weak listeners
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}
