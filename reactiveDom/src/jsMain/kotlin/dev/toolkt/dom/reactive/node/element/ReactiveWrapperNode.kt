package dev.toolkt.dom.reactive.node.element

import dev.toolkt.dom.reactive.node.ReactiveNode
import org.w3c.dom.Node

class ReactiveWrapperNode(
    override val rawNode: Node,
) : ReactiveNode()
