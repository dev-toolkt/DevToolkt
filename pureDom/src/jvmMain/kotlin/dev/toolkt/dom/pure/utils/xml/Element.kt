package dev.toolkt.dom.pure.utils.xml

import dev.toolkt.dom.pure.utils.xml.svg.asList
import org.w3c.dom.Element

val Element.childElements: List<Element>
    get() = this.childNodes.asList().filterIsInstance<Element>()

fun Element.getAttributeOrNull(name: String): String? = when {
    hasAttribute(name) -> getAttribute(name)
    else -> null
}
