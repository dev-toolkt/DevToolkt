package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.event_stream.EventSource.TargetedWeakListener

class LoopedEventStream<E>() : ProperEventStream<E>() {
    sealed class PlaceholderSubscription<E> : Subscription {
        abstract fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        )
    }

    class PlaceholderStrongSubscription<E>(
        initialBufferedListener: Listener<E>,
    ) : PlaceholderSubscription<E>() {
        class BufferedSubscription<E>(
            var bufferedListener: Listener<E>?,
        ) : Subscription {
            fun loop(
                eventStream: EventStream<E>,
                initialEvent: E?,
            ): Subscription? {
                val bufferedListener = this.bufferedListener ?: return null

                if (initialEvent != null) {
                    bufferedListener(initialEvent)
                }

                return eventStream.listen(listener = bufferedListener)
            }

            override fun cancel() {
                if (bufferedListener == null) {
                    throw IllegalStateException("The subscription is already cancelled")
                }

                bufferedListener = null
            }
        }

        private var innerSubscription: Subscription? = BufferedSubscription(
            bufferedListener = initialBufferedListener,
        )

        override fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        ) {
            @Suppress("UNCHECKED_CAST") val bufferedSubscription = innerSubscription as? BufferedSubscription<E>
                ?: throw IllegalStateException("The subscription is already looped")

            innerSubscription = bufferedSubscription.loop(
                eventStream = eventStream,
                initialEvent = initialEvent,
            )
        }

        override fun cancel() {
            val innerSubscription =
                this.innerSubscription ?: throw IllegalStateException("The subscription is already cancelled")

            innerSubscription.cancel()
        }
    }

    class PlaceholderWeakSubscription<E>(
        initialBufferedListener: TargetedWeakListener<Any, E>,
    ) : PlaceholderSubscription<E>() {
        class BufferedSubscription<E>(
            var bufferedTargetedListener: TargetedWeakListener<Any, E>?,
        ) : Subscription {
            fun loop(
                eventStream: EventStream<E>,
                initialEvent: E?,
            ): Subscription? {
                val bufferedListener = this.bufferedTargetedListener ?: return null

                if (initialEvent != null) {
                    val (target, weakListener) = bufferedListener

                    weakListener(target, initialEvent)
                }

                return eventStream.listenWeak(targetedWeakListener = bufferedListener)
            }

            override fun cancel() {
                if (bufferedTargetedListener == null) {
                    throw IllegalStateException("The subscription is already cancelled")
                }

                bufferedTargetedListener = null
            }
        }

        private var innerSubscription: Subscription? = BufferedSubscription(
            bufferedTargetedListener = initialBufferedListener,
        )

        override fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        ) {
            @Suppress("UNCHECKED_CAST") val bufferedSubscription = innerSubscription as? BufferedSubscription<E>
                ?: throw IllegalStateException("The subscription is already looped")

            innerSubscription = bufferedSubscription.loop(
                eventStream = eventStream,
                initialEvent = initialEvent,
            )
        }

        override fun cancel() {
            val innerSubscription =
                this.innerSubscription ?: throw IllegalStateException("The subscription is already cancelled")

            innerSubscription.cancel()
        }
    }

    class PlaceholderEventStream<E> : ProperEventStream<E>() {
        private val placeholderSubscriptions = mutableSetOf<PlaceholderSubscription<E>>()

        override fun listen(listener: Listener<E>): Subscription {
            val placeholderSubscription = PlaceholderStrongSubscription(initialBufferedListener = listener)

            placeholderSubscriptions.add(placeholderSubscription)

            return placeholderSubscription
        }

        override fun <T : Any> listenWeak(
            target: T,
            listener: WeakListener<T, E>,
        ): Subscription {
            val targetedWeakListener = TargetedWeakListener(
                target = target,
                listener = listener,
            )

            @Suppress("UNCHECKED_CAST") val placeholderSubscription = PlaceholderWeakSubscription(
                initialBufferedListener = targetedWeakListener as TargetedWeakListener<Any, E>,
            )

            placeholderSubscriptions.add(placeholderSubscription)

            return placeholderSubscription
        }

        fun loop(
            eventStream: EventStream<E>,
            initialEvent: E?,
        ) {
            placeholderSubscriptions.forEach { subscription ->
                subscription.loop(
                    eventStream = eventStream,
                    initialEvent = initialEvent,
                )
            }
        }
    }

    private var innerEventStream: EventStream<E> = PlaceholderEventStream()

    fun loop(
        eventStream: EventStream<E>,
        initialEvent: E? = null,
    ) {
        val placeholderEventStream = this.innerEventStream as? PlaceholderEventStream<E>
            ?: throw IllegalStateException("The stream is already looped")

        placeholderEventStream.loop(
            eventStream = eventStream,
            initialEvent = initialEvent,
        )

        innerEventStream = eventStream
    }

    override fun listen(
        listener: Listener<E>,
    ): Subscription = innerEventStream.listen(
        listener = listener,
    )

    override fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, E>,
    ): Subscription = innerEventStream.listenWeak(
        target = target,
        listener = listener,
    )
}
