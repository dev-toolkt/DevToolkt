package dev.toolkt.dom.reactive

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import org.w3c.dom.events.EventTarget

class ReactiveButtonElement(
    override val children: ReactiveList<ReactiveNode>,
    private val handleClick: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement() {
    override val elementName: String = "button"

    private val onClickEmitter = EventEmitter<ReactiveMouseEvent>()

    val onClick: EventEmitter<ReactiveMouseEvent>
        get() = onClickEmitter

    override fun attachEventHandlers(target: EventTarget) {
        handleClick.attach(
            target = target,
            eventName = "click",
            wrapper = ReactiveMouseEvent.Companion,
            emitter = onClickEmitter,
        )
    }

    init {
        rawElement
    }
}
