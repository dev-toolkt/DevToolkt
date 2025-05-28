package dev.toolkt.dom.reactive.node

import dev.toolkt.dom.reactive.utils.createReactiveTextNode
import dev.toolkt.reactive.cell.Cell
import kotlinx.browser.document
import org.w3c.dom.Text

class ReactiveTextNode(
    val data: Cell<String>,
) : ReactiveNode() {
    override val rawNode: Text = document.createReactiveTextNode(
        data = data,
    )
}
