package dev.toolkt.dom.pure

import dev.toolkt.core.numeric.NumericObject
import org.w3c.dom.Document
import org.w3c.dom.Element

abstract class PureElement : NumericObject {
    abstract fun toRawElement(
        document: Document,
    ): Element
}
