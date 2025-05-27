package dev.toolkt.dom.reactive.node.element

import dev.toolkt.dom.reactive.node.ReactiveNode
import org.w3c.dom.Element
import org.w3c.dom.Node

abstract class ReactiveElement : ReactiveNode() {
    final override val rawNode: Node
        get() = rawElement

    abstract val rawElement: Element
}
