package dev.toolkt.reactive.vertices

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

interface StrongEventSource<out T> {
    fun addStrongListener(
        listener: Listener<T>,
    )

    fun removeStrongListener(
        listener: Listener<T>,
    )
}

interface WeakEventSource<out T> {
    fun addWeakListener(
        listener: Listener<T>,
    )

    fun removeWeakListener(
        listener: Listener<T>,
    )
}

/**
 * A vertex in the functional-reactive dependency graph. Allows abstracting the
 * implementation details from the public interface and sharing common behavior
 * between cells, event stream and dynamic collections internals, though from
 * the public interface perspective these entities are strongly distinct.
 */
abstract class Vertex<out T>() : StrongEventSource<T>, WeakEventSource<T> {
    interface ListenerToken

    data class IdentityListenerToken<T>(
        val listener: Listener<T>,
    ) : ListenerToken

    sealed class ListenerStrength {
        data object Strong : ListenerStrength() {
            override fun <T> addListener(
                vertex: Vertex<T>,
                listener: Listener<T>,
            ): ListenerToken {
                vertex.addStrongListener(listener = listener)

                return IdentityListenerToken(listener = listener)
            }

            override fun <T> removeListener(
                vertex: Vertex<T>,
                token: ListenerToken,
            ) {
                @Suppress("UNCHECKED_CAST") (token as IdentityListenerToken<T>)

                vertex.removeStrongListener(listener = token.listener)
            }
        }

        data object Weak : ListenerStrength() {
            override fun <T> addListener(
                vertex: Vertex<T>,
                listener: Listener<T>,
            ): ListenerToken {
                vertex.addWeakListener(listener = listener)

                return IdentityListenerToken(listener = listener)
            }

            override fun <T> removeListener(
                vertex: Vertex<T>,
                token: ListenerToken,
            ) {
                @Suppress("UNCHECKED_CAST") (token as IdentityListenerToken<T>)

                vertex.removeWeakListener(listener = token.listener)
            }
        }

        abstract fun <T> addListener(
            vertex: Vertex<T>,
            listener: Listener<T>,
        ): ListenerToken

        abstract fun <T> removeListener(
            vertex: Vertex<T>,
            token: ListenerToken,
        )
    }

    fun subscribeStrongRaw(
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

    /**
     * Subscribes a [listener] that can switch between strong and weak.
     *
     * @param initialStrength the initial strength of the listener
     */
    fun subscribeHybridRaw(
        initialStrength: ListenerStrength = ListenerStrength.Weak,
        listener: Listener<T>,
    ): HybridSubscription {
        val initialToken = initialStrength.addListener(
            vertex = this,
            listener = listener,
        )

        onSubscribed()

        return object : HybridSubscription {
            private var token: ListenerToken = initialToken

            private var currentStrength = initialStrength

            override fun cancel() {
                currentStrength.removeListener(
                    vertex = this@Vertex,
                    token = token,
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
                    token = token,
                )

                token = newStrength.addListener(
                    vertex = this@Vertex,
                    listener = listener,
                )

                currentStrength = newStrength
            }
        }
    }

    protected abstract fun onSubscribed()

    protected abstract fun onUnsubscribed()
}
