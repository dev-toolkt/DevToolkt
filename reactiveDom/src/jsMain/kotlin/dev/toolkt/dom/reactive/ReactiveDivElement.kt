package dev.toolkt.dom.reactive

import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Element

class ReactiveDivElement(
    override val children: ReactiveList<ReactiveNode>,
    style: ReactiveStyle? = null,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    style = style,
    handleMouseDown = handleMouseDown,
) {
    override val elementName: String = "div"

    override fun setupElement(element: Element) {
    }

    init {
        rawElement
    }
}
