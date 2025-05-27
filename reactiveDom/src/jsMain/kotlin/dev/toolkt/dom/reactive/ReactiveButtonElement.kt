package dev.toolkt.dom.reactive

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.dom.reactive.html.HtmlEventHandler
import dev.toolkt.dom.reactive.html.HtmlMouseEvent
import dev.toolkt.dom.reactive.html.attach
import org.w3c.dom.events.EventTarget

class ReactiveButtonElement(
    override val children: ReactiveList<ReactiveNode>,
    private val handleClick: HtmlEventHandler<HtmlMouseEvent> = HtmlEventHandler.Accepting,
) : ReactiveGenericHtmlElement() {
    override val elementName: String = "button"

    private val onClickEmitter = EventEmitter<HtmlMouseEvent>()

    val onClick: EventEmitter<HtmlMouseEvent>
        get() = onClickEmitter

    override fun attachEventHandlers(target: EventTarget) {
        handleClick.attach(
            target = target,
            eventName = "click",
            wrapper = HtmlMouseEvent.Companion,
            emitter = onClickEmitter,
        )
    }

    init {
        rawElement
    }
}
