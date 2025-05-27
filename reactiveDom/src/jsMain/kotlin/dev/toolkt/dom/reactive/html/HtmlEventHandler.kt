package dev.toolkt.dom.reactive.html

import dev.toolkt.reactive.event_stream.EventEmitter
import org.w3c.dom.events.EventTarget

interface HtmlEventHandler<in E : HtmlEvent> {
    object Accepting : HtmlEventHandler<HtmlEvent> {
        override fun handle(event: HtmlEvent): Resolution = Resolution.Accept
    }

    enum class Resolution {
        Accept, PreventDefault,
    }

    fun handle(event: E): Resolution
}

fun <E : HtmlEvent> HtmlEventHandler<E>.attach(
    target: EventTarget,
    eventName: String,
    wrapper: HtmlEvent.Wrapper<E>,
    emitter: EventEmitter<E>,
) {
    target.addEventListener(
        type = eventName,
        callback = { rawEvent ->
            val wrappedEvent = wrapper.wrap(rawEvent = rawEvent)
            val resolution = handle(wrappedEvent)

            if (resolution == HtmlEventHandler.Resolution.PreventDefault) {
                rawEvent.preventDefault()
            } else {
                emitter.emit(wrappedEvent)
            }
        },
    )
}
