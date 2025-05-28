package dev.toolkt.dom.reactive.utils

import dev.toolkt.reactive.cell.Cell
import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.Text

fun Document.createReactiveTextNode(
    data: Cell<String>,
): Text = data.formAndForget(
    create = { initialValue: String ->
        this.createTextNode(
            data = initialValue,
        )
    },
    update = { textNode: Text, newValue: String ->
        textNode.data = newValue
    },
)
