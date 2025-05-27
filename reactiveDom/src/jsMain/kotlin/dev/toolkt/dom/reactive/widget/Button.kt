package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.reactive.node.ReactiveTextNode
import dev.toolkt.dom.reactive.node.element.ReactiveButtonElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Node

class Button private constructor(
    private val buttonElement: ReactiveButtonElement,
) : Widget() {
    companion object {
        fun of(
            text: Cell<String>,
        ) = Button(
            buttonElement = ReactiveButtonElement(
                children = ReactiveList.Companion.of(
                    ReactiveTextNode(
                        data = text,
                    ),
                ),
            ),
        )
    }

    val onPressed: EventStream<Unit>
        get() = buttonElement.onClick.map { Unit }

    override val rawNode: Node
        get() = buttonElement.rawNode
}
