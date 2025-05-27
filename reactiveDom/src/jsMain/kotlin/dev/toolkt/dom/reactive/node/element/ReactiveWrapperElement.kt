package dev.toolkt.dom.reactive.node.element

import org.w3c.dom.Element

class ReactiveWrapperElement(
    override val rawElement: Element,
) : ReactiveElement()
