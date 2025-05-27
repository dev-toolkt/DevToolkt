package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.reactive.node.element.ReactiveWrapperNode
import org.w3c.dom.Node

abstract class Widget {
    abstract val rawNode: Node

    val asReactiveElement: ReactiveWrapperNode
        get() = ReactiveWrapperNode(
            rawNode = rawNode,
        )
}
