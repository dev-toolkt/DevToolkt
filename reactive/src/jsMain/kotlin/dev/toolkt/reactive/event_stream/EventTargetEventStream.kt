package dev.toolkt.reactive.event_stream

import dev.toolkt.reactive.Subscription
import org.w3c.dom.AddEventListenerOptions
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventTarget

class EventTargetEventStream(
    private val eventTarget: EventTarget,
    private val type: String,
) : DependentEventStream<Event>() {
    override fun observe(): Subscription {
        fun callback(
            event: Event,
        ) {
            notify(event = event)
        }

        eventTarget.addEventListener(
            type = type,
            callback = ::callback,
            options = AddEventListenerOptions(
                passive = true,
            ),
        )

        return object : Subscription {
            override fun cancel() {
                eventTarget.removeEventListener(
                    type = type,
                    callback = ::callback,
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
