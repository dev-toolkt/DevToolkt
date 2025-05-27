package dev.toolkt.dom.reactive

import org.w3c.dom.Element
import org.w3c.dom.Node

abstract class ReactiveHtmlElement : ReactiveNode() {
    final override val rawNode: Node
        get() = rawElement

    abstract val rawElement: Element
}
