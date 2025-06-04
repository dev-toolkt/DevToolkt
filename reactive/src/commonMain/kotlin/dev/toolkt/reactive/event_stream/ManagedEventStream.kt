package dev.toolkt.reactive.event_stream

import dev.toolkt.core.collections.insertEffectively
import dev.toolkt.core.collections.insertEffectivelyWeak
import dev.toolkt.core.collections.removeEffectively
import dev.toolkt.core.platform.mutableWeakMapOf
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

abstract class ManagedEventStream<out E> : ProperEventStream<E>() {
    enum class State {
        Paused, Resumed,
    }

    private val listeners = mutableSetOf<Listener<E>>()

    private val weakListeners = mutableWeakMapOf<Any, WeakListener<Any, E>>()

    final override fun listen(
        listener: Listener<E>,
    ): Subscription {
        val remover = listeners.insertEffectively(listener)

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                remover.removeEffectively()

                potentiallyPause()
            }
        }
    }

    final override fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription {
        val remover = weakListeners.insertEffectivelyWeak(
            key = target,
            value = @Suppress("UNCHECKED_CAST") (listener as WeakListener<Any, E>),
        )

        potentiallyResume()

        return object : Subscription {
            override fun cancel() {
                // We don't check whether the entry was successfully removed,
                // as the entry might've been purged if the target was collected
                remover.remove()
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
