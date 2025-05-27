package dev.toolkt.dom.pure.utils.xml.svg

import org.w3c.dom.Node
import org.w3c.dom.NodeList

fun NodeList.asList(): List<Node> = object : AbstractList<Node>() {
    override val size: Int
        get() = length

    override fun get(
        index: Int,
    ): Node = item(index) ?: throw IndexOutOfBoundsException()
}
