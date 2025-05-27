package dev.toolkt.dom.pure.utils.xml.svg

import org.w3c.dom.DOMException
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegList

fun SVGPathSegList.asList(): List<SVGPathSeg> = object : AbstractList<SVGPathSeg>() {
    override val size: Int
        get() = numberOfItems

    override fun get(
        index: Int,
    ): SVGPathSeg {
        try {
            return getItem(index)
        } catch (e: DOMException) {
            throw IndexOutOfBoundsException()
        }
    }
}
