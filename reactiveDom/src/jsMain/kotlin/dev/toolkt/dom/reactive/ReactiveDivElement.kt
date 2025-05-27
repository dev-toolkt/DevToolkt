package dev.toolkt.dom.reactive

import dev.toolkt.reactive.reactive_list.ReactiveList
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import org.w3c.dom.events.EventTarget

class ReactiveDivElement(
    override val children: ReactiveList<ReactiveNode>,
    private val handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement() {
    override val elementName: String = "div"

    private val onMouseDownEmitter = EventEmitter<ReactiveMouseEvent>()

    val onMouseDown: EventEmitter<ReactiveMouseEvent>
        get() = onMouseDownEmitter

    override fun attachEventHandlers(
        target: EventTarget,
    ) {
        handleMouseDown.attach(
            target = target,
            eventName = "mouseDown",
            wrapper = ReactiveMouseEvent.Companion,
            emitter = onMouseDownEmitter,
        )
    }

    init {
        rawElement
    }
}
