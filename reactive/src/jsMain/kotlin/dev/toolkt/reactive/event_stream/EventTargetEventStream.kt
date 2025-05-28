package dev.toolkt.reactive.event_stream

import dev.toolkt.core.platform.WeakRef
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

class EventTargetEventStream(
    private val eventTarget: EventTarget,
    private val type: String,
) : ForeignEventStream<Event>() {
    override fun listen(
        listener: Listener<Event>,
    ): Subscription {
        eventTarget.addEventListener(
            type = type,
            callback = listener,
            options = AddEventListenerOptions(
                passive = true,
            ),
        )

        return object : Subscription {
            override fun cancel() {
                eventTarget.removeEventListener(
                    type = type,
                    callback = listener,
                )
            }
        }
    }

    override fun <T : Any> listenWeak(
        target: T,
        listener: WeakListener<T, Event>
    ): Subscription {
        val targetWeakRef = WeakRef(target)

        fun handle(event: Event) {
            when (val target = targetWeakRef.deref()) {
                null -> {
                    eventTarget.removeEventListener(
                        type = type,
                        callback = ::handle,
                    )
                }

                else -> {
                    listener(target, event)
                }
            }
        }

        eventTarget.addEventListener(
            type = type,
            callback = ::handle,
            options = AddEventListenerOptions(
                passive = true,
            ),
        )

        return object : Subscription {
            override fun cancel() {
                eventTarget.removeEventListener(
                    type = type,
                    callback = ::handle,
                )
            }
        }
    }
}

fun EventTarget.getEventStream(
    type: String,
): EventStream<Event> = EventTargetEventStream(
    eventTarget = this,
    type = type,
)
