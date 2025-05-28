package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.reactive.utils.createReactiveHtmlInputElement
import dev.toolkt.dom.reactive.utils.getChangeEventStream
import dev.toolkt.dom.reactive.utils.getClickEventStream
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node

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

    val onCheckStateChanged by lazy {
        inputElement.getChangeEventStream().map {
            val target = it.target as HTMLInputElement
            target.checked
        }
    }

    val isCheckedNow: Boolean
        get() = inputElement.checked

    val isChecked: Cell<Boolean>
        get() = onCheckStateChanged.hold(initialValue = inputElement.checked)

    override val rawNode: Node
        get() = inputElement
}
