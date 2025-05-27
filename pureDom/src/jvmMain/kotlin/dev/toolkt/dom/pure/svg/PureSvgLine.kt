package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import org.w3c.dom.Document
import org.w3c.dom.Element

data class PureSvgLine(
    val start: Point,
    val end: Point,
    override val stroke: Stroke = Stroke.default,
    override val markerEndId: String? = null,
) : PureSvgShape() {
    override val fill: Fill.Specified? = null

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("line").apply {
        setAttribute("x1", start.x.toString())
        setAttribute("y1", start.y.toString())
        setAttribute("x2", end.x.toString())
        setAttribute("y2", end.y.toString())

        setupRawShape(element = this)
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is PureSvgLine -> false
        !start.equalsWithTolerance(other.start, tolerance) -> false
        !end.equalsWithTolerance(other.end, tolerance) -> false
        !stroke.equalsWithTolerance(other.stroke, tolerance) -> false
        else -> true
    }

    override fun transformVia(
        transformation: Transformation,
    ): PureSvgLine = PureSvgLine(
        start = transformation.transform(point = start),
        end = transformation.transform(point = end),
        stroke = stroke,
    )
}
