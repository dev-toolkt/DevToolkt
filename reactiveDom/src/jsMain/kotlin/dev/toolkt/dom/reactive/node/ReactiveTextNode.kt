package dev.toolkt.dom.reactive.node

import dev.toolkt.reactive.cell.Cell
import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.Text

class ReactiveTextNode(
    val data: Cell<String>,
) : ReactiveNode() {
    override val rawNode: Text = document.createReactiveTextNode(
        data = data,
    )
}

fun Document.createReactiveTextNode(
    data: Cell<String>,
): Text = data.formAndForget(
    create = { initialValue: String ->
        document.createTextNode(
            data = initialValue,
        )
    },
    update = { textNode: Text, newValue: String ->
        textNode.data = newValue
    },
)
