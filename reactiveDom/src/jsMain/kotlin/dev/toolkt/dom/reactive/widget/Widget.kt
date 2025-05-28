package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.reactive.node.element.ReactiveWrapperNode
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Node

abstract class Widget {
    abstract val rawNode: Node

    val asReactiveElement: ReactiveWrapperNode
        get() = ReactiveWrapperNode(
            rawNode = rawNode,
        )
}

val ReactiveList<Widget>.rawNodes: ReactiveList<Node>
    get() = this.map { it.rawNode }
