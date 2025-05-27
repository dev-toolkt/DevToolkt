package dev.toolkt.dom.reactive

import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Element

class ReactiveDivElement(
    override val children: ReactiveList<ReactiveNode>,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    handleMouseDown = handleMouseDown,
) {
    override val elementName: String = "div"

    override fun attachEventHandlers(element: Element) {
    }

    init {
        rawElement
    }
}
