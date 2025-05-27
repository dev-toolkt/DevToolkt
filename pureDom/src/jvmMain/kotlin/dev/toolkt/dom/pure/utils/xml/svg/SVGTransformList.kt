package dev.toolkt.dom.pure.utils.xml.svg

import org.w3c.dom.DOMException
import org.w3c.dom.svg.SVGTransform
import org.w3c.dom.svg.SVGTransformList

fun SVGTransformList.asList(): List<SVGTransform> = object : AbstractList<SVGTransform>() {
    override val size: Int
        get() = numberOfItems

    override fun get(
        index: Int,
    ): SVGTransform {
        try {
            return getItem(index)
        } catch (e: DOMException) {
            throw IndexOutOfBoundsException()
        }
    }
}
