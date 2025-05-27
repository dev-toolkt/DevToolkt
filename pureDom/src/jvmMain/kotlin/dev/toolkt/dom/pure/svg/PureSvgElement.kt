package dev.toolkt.dom.pure.svg

import dev.toolkt.dom.pure.PureElement
import dev.toolkt.geometry.transformations.Transformation
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGRectElement

abstract class PureSvgElement : PureElement() {
    companion object {
        const val SVG_NS = "http://www.w3.org/2000/svg"
    }

    protected fun Document.createSvgElement(
        name: String,
    ): Element = createElementNS(SVG_NS, "svg:$name")
}

abstract class PureSvgGraphicsElement : PureSvgElement() {
    abstract fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape>
}

fun Element.toSvgGraphicsElements(): PureSvgGraphicsElement? = when (this) {
    is SVGPathElement -> toPurePath()
    is SVGGElement -> toPureGroup()
    is SVGRectElement -> toPureRect()
    else -> null
}
