package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

abstract class ManagedEventStreamNg<E> : ActiveEventStreamNg<E>() {
    final override fun listen(
        listener: Listener<E>,
    ): Subscription {
        TODO("Not yet implemented")
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
        TODO()
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}
