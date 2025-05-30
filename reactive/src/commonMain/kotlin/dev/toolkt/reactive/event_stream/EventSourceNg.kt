package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription

interface EventSourceNg<out E> {
    data class TargetedWeakListener<T, E>(
        val target: T,
        val listener: WeakListener<T, E>,
    )

    fun listen(
        listener: Listener<E>,
    ): Subscription

    fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription
}

fun <T : Any, E> EventSourceNg<E>.listenWeak(
    targetedWeakListener: EventSourceNg.TargetedWeakListener<T, E>,
): Subscription = listenWeak(
    target = targetedWeakListener.target,
    listener = targetedWeakListener.listener,
)
