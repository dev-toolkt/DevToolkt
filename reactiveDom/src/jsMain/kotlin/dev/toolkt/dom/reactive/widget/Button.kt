package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlButtonElement
import dev.toolkt.dom.reactive.utils.html.getClickEventStream
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import kotlinx.browser.document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.Node

class Button private constructor(
    private val buttonElement: HTMLButtonElement,
) : Widget() {
    companion object {
        fun of(
            text: Cell<String>,
        ): Button = Button(
            buttonElement = document.createReactiveHtmlButtonElement(),
        )
    }

    val onPressed: EventStream<Unit>
        get() = buttonElement.getClickEventStream().units()

    override val rawNode: Node
        get() = buttonElement
}
