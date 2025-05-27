package dev.toolkt.dom.reactive

import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Element

class ReactiveButtonElement(
    override val children: ReactiveList<ReactiveNode>,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
    private val handleClick: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    handleMouseDown = handleMouseDown,
) {
    override val elementName: String = "button"

    private val onClickEmitter = EventEmitter<ReactiveMouseEvent>()

    val onClick: EventEmitter<ReactiveMouseEvent>
        get() = onClickEmitter

    override fun attachEventHandlers(element: Element) {
        handleClick.attach(
            target = element,
            eventName = "click",
            wrapper = ReactiveMouseEvent.Companion,
            emitter = onClickEmitter,
        )
    }


    init {
        rawElement
    }
}
