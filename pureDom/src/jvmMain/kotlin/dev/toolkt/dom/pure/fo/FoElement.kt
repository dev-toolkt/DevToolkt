package dev.toolkt.dom.pure.fo

import dev.toolkt.dom.pure.PureElement
import org.apache.fop.fo.FOElementMapping
import org.w3c.dom.Document
import org.w3c.dom.Element

abstract class FoElement : PureElement() {
    companion object {
        const val FO_NS = FOElementMapping.URI
    }

    protected fun Document.createFoElement(
        name: String,
    ): Element = createElementNS(FO_NS, "fo:$name")
}
