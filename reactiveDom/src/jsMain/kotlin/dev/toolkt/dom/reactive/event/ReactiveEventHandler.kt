package dev.toolkt.dom.reactive.event

import dev.toolkt.reactive.event_stream.EventEmitter
import org.w3c.dom.events.EventTarget

interface ReactiveEventHandler<in E : ReactiveEvent> {
    object Accepting : ReactiveEventHandler<ReactiveEvent> {
        override fun handle(event: ReactiveEvent): Resolution = Resolution.Accept
    }

    enum class Resolution {
        Accept, PreventDefault,
    }

    fun handle(event: E): Resolution
}

fun <E : ReactiveEvent> ReactiveEventHandler<E>.attach(
    target: EventTarget,
    eventName: String,
    wrapper: ReactiveEvent.Wrapper<E>,
    emitter: EventEmitter<E>,
) {
    target.addEventListener(
        type = eventName,
        callback = { rawEvent ->
            val wrappedEvent = wrapper.wrap(rawEvent = rawEvent)
            val resolution = handle(wrappedEvent)

            if (resolution == ReactiveEventHandler.Resolution.PreventDefault) {
                rawEvent.preventDefault()
            } else {
                emitter.emit(wrappedEvent)
            }
        },
    )
}
