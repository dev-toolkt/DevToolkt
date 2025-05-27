package dev.toolkt.dom.reactive.node.element

import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import dev.toolkt.dom.reactive.node.ReactiveNode
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Element

class ReactiveButtonElement(
    override val children: ReactiveList<ReactiveNode>,
    style: ReactiveStyle? = null,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent>? = null,
    private val handleClick: ReactiveEventHandler<ReactiveMouseEvent>? = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    style = style,
    handleMouseDown = handleMouseDown,
) {
    override val elementName: String = "button"

    private val onClickEmitter = EventEmitter<ReactiveMouseEvent>()

    val onClick: EventStream<ReactiveMouseEvent>
        get() = onClickEmitter

    override fun setupElement(element: Element) {
        handleClick?.attach(
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

