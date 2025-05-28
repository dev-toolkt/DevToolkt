package dev.toolkt.reactive.vertices

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.RawListener
import dev.toolkt.reactive.Subscription

/**
 * A vertex in the functional-reactive dependency graph. Allows abstracting the
 * implementation details from the public interface and sharing common behavior
 * between cells, event stream and dynamic collections internals, though from
 * the public interface perspective these entities are strongly distinct.
 */
abstract class Vertex<T>() {
    sealed class ListenerStrength {
        data object Strong : ListenerStrength() {
            override fun <T> addListener(
                vertex: Vertex<T>,
                listener: Listener<T>,
            ) {
                vertex.addStrongListener(listener = listener)
            }

            override fun <T> removeListener(
                vertex: Vertex<T>,
                listener: Listener<T>,
            ) {
                vertex.removeStrongListener(listener = listener)
            }
        }

        data object Weak : ListenerStrength() {
            override fun <T> addListener(
                vertex: Vertex<T>,
                listener: Listener<T>,
            ) {
                vertex.addWeakListener(listener = listener)
            }

            override fun <T> removeListener(
                vertex: Vertex<T>,
                listener: Listener<T>,
            ) {
                vertex.removeWeakListener(listener = listener)
            }
        }

        abstract fun <T> addListener(
            vertex: Vertex<T>,
            listener: Listener<T>,
        )

        abstract fun <T> removeListener(
            vertex: Vertex<T>,
            listener: Listener<T>,
        )
    }

    fun subscribeStrongRaw(
        listener: RawListener<T>,
    ): Subscription = subscribeStrong(
        listener = object : Listener<T> {
            override fun handle(event: T) {
                listener(event)
            }
        },
    )

    private fun subscribeStrong(
        listener: Listener<T>,
    ): Subscription {
        addStrongListener(listener = listener)

        onSubscribed()

        return object : Subscription {
            override fun cancel() {
                removeStrongListener(listener = listener)

                onUnsubscribed()
            }
        }
    }

    fun subscribeHybridRaw(
        initialStrength: ListenerStrength = ListenerStrength.Weak,
        listener: RawListener<T>,
    ): HybridSubscription = subscribeHybrid(
        listener = object : Listener<T> {
            override fun handle(event: T) {
                listener(event)
            }
        },
        initialStrength = initialStrength,
    )

    /**
     * Subscribes a [listener] that can switch between strong and weak.
     *
     * @param initialStrength the initial strength of the listener
     */
    private fun subscribeHybrid(
        listener: Listener<T>,
        initialStrength: ListenerStrength = ListenerStrength.Weak,
    ): HybridSubscription {
        initialStrength.addListener(
            vertex = this,
            listener = listener,
        )

        onSubscribed()

        return object : HybridSubscription {
            private var currentStrength = initialStrength

            override fun cancel() {
                currentStrength.removeListener(
                    vertex = this@Vertex,
                    listener = listener,
                )

                onUnsubscribed()
            }

            override fun updateStrength(
                newStrength: ListenerStrength,
            ) {
                if (currentStrength == newStrength) {
                    throw AssertionError("The strength is already set to $newStrength (???)")
                }

                currentStrength.removeListener(
                    vertex = this@Vertex,
                    listener = listener,
                )

                newStrength.addListener(
                    vertex = this@Vertex,
                    listener = listener,
                )

                currentStrength = newStrength
            }
        }
    }

    abstract fun addStrongListener(
        listener: Listener<T>,
    )

    abstract fun removeStrongListener(
        listener: Listener<T>,
    )

    abstract fun addWeakListener(
        listener: Listener<T>,
    )

    abstract fun removeWeakListener(
        listener: Listener<T>,
    )

    protected abstract fun onSubscribed()

    protected abstract fun onUnsubscribed()
}
