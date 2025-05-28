package dev.toolkt.reactive.vertices

import dev.toolkt.reactive.Listener
import dev.toolkt.core.platform.mutableWeakSetOf

abstract class ManagedVertex<T> : Vertex<T>() {
    enum class State {
        Paused, Resumed,
    }

    companion object {
        private var nextId = 0
    }

    @Suppress("unused")
    private val id: Int = nextId++

    protected abstract val kind: String

    protected val tag: String
        get() = "$kind#$id"

    private var state = State.Paused

    private val strongListeners = mutableSetOf<Listener<T>>()

    private val weakListeners = mutableWeakSetOf<Listener<T>>()

    private fun hasListeners() = strongListeners.isNotEmpty() || weakListeners.isNotEmpty()

    protected fun notify(
        value: T,
    ) {
        strongListeners.forEach {
            it(value)
        }

        weakListeners.forEach {
            it(value)
        }

        // Touching the weak listeners set could purge all (unreachable) listeners
        pauseIfLostListeners()
    }

    final override fun addStrongListener(
        listener: Listener<T>,
    ) {
        val wasAdded = strongListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already strongly-subscribed (???)")
    }

    final override fun removeStrongListener(
        listener: Listener<T>,
    ) {
        val wasRemoved = strongListeners.remove(listener)

        if (!wasRemoved) throw AssertionError("Listener is not strongly-subscribed (???)")
    }

    final override fun addWeakListener(
        listener: Listener<T>,
    ) {
        val wasAdded = weakListeners.add(listener)

        if (!wasAdded) throw AssertionError("Listener is already weakly-subscribed (???)")
    }

    final override fun removeWeakListener(
        listener: Listener<T>,
    ) {
        val wasRemoved = weakListeners.remove(listener)

        if (!wasRemoved) throw AssertionError("Listener is not weakly-subscribed (???)")
    }

    final override fun onSubscribed() {
        resumeIfPaused()
    }

    final override fun onUnsubscribed() {
        pauseIfLostListeners()
    }

    private fun resumeIfPaused() {
        if (state == State.Paused) {
            onResumed()

            state = State.Resumed
        }
    }

    private fun pauseIfLostListeners() {
        if (!hasListeners() && state == State.Resumed) {
            onPaused()

            state = State.Paused
        }
    }

    protected abstract fun onResumed()

    protected abstract fun onPaused()
}
