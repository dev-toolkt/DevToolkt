package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.reactive.utils.createReactiveHtmlInputElement
import dev.toolkt.dom.reactive.utils.getChangeEventStream
import dev.toolkt.dom.reactive.utils.getCheckedCell
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event

class Checkbox private constructor(
    private val inputElement: HTMLInputElement,
) : Widget() {
    companion object {
        fun of(
            text: Cell<String>,
        ): Checkbox = Checkbox(
            inputElement = document.createReactiveHtmlInputElement(type = "checkbox"),
        )
    }

    val onCheckStateChanged: EventStream<Event>
        get() = inputElement.getChangeEventStream()

    val isCheckedNow: Boolean
        get() = inputElement.checked

    val isChecked: Cell<Boolean>
        get() = inputElement.getCheckedCell()

    override val rawNode: Node
        get() = inputElement
}
