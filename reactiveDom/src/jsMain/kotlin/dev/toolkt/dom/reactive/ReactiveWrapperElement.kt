package dev.toolkt.dom.reactive

import org.w3c.dom.Element

class ReactiveWrapperElement(
    override val rawElement: Element,
) : ReactiveHtmlElement()
