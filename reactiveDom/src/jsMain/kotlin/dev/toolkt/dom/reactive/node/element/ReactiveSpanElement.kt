package dev.toolkt.dom.reactive.node.element

import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.node.ReactiveNode
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Element

class ReactiveSpanElement(
    override val children: ReactiveList<ReactiveNode>,
    style: ReactiveStyle? = null,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    style = style,
    handleMouseDown = handleMouseDown,
) {
    override val elementName: String = "span"

    override fun setupElement(element: Element) {
    }

    init {
        rawElement
    }
}
