package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.mutableWeakMapOf
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

abstract class ManagedEventStream<out E> : ProperEventStream<E>() {
    enum class State {
        Paused,
        Resumed,
    }

    private val listeners = mutableSetOf<Listener<E>>()

    private val weakListeners = mutableWeakMapOf<Any, WeakListener<Any, E>>()

    final override fun listen(
        listener: Listener<E>,
    ): Subscription {
        val wasAdded = listeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already registered")

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                val wasRemoved = listeners.remove(listener)

                if (!wasRemoved) throw AssertionError("Listener wasn't registered")

                potentiallyPause()
            }
        }
    }

    final override fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription {
        @Suppress("UNCHECKED_CAST")
        val handle = weakListeners.add(
            key = target,
            value = listener as WeakListener<Any, E>,
        )

        if (handle == null) {
            throw AssertionError("Weak listener for target $target is already registered")
        }

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                // We don't check whether the handle was successfully removed,
                // as the entry might've been purged if the target was collected
                weakListeners.remove(handle)
            }
        }
    }

    private val listenerCount: Int
        get() = listeners.size + weakListeners.size

    protected val state: State
        get() = when {
            listenerCount > 0 -> State.Resumed
            else -> State.Paused
        }

    private fun potentiallyResume() {
        if (listenerCount == 1) {
            onResumed()
        }
    }

    private fun potentiallyPause() {
        if (listenerCount == 0) {
            onPaused()
        }
    }

    protected fun notify(
        event: @UnsafeVariance E,
    ) {
        listeners.forEach {
            it(event)
        }

        if (weakListeners.isEmpty()) {
            return
        }

        weakListeners.forEach { (target, weakListener) ->
            weakListener(target, event)
        }

        // Iterating over the weak map may trigger unreachable entry purging,
        // the listener count may have reached zero
        potentiallyPause()
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}
