package dev.toolkt.reactive.event_stream_ng

import dev.toolkt.core.platform.WeakRef
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.Subscription
import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

class EventTargetEventStreamNg(
    private val eventTarget: EventTarget,
    private val type: String,
) : ForeignEventStreamNg<Event>() {
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
) = EventTargetEventStreamNg(
    eventTarget = this,
    type = type,
)
