package dev.toolkt.dom.reactive

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.dom.reactive.html.HtmlEventHandler
import dev.toolkt.dom.reactive.html.HtmlMouseEvent
import dev.toolkt.dom.reactive.html.attach
import org.w3c.dom.events.EventTarget

class ReactiveDivElement(
    override val children: ReactiveList<ReactiveNode>,
    private val handleMouseDown: HtmlEventHandler<HtmlMouseEvent> = HtmlEventHandler.Accepting,
) : ReactiveGenericHtmlElement() {
    override val elementName: String = "div"

    private val onMouseDownEmitter = EventEmitter<HtmlMouseEvent>()

    val onMouseDown: EventEmitter<HtmlMouseEvent>
        get() = onMouseDownEmitter

    override fun attachEventHandlers(
        target: EventTarget,
    ) {
        handleMouseDown.attach(
            target = target,
            eventName = "mouseDown",
            wrapper = HtmlMouseEvent.Companion,
            emitter = onMouseDownEmitter,
        )
    }

    init {
        rawElement
    }
}
