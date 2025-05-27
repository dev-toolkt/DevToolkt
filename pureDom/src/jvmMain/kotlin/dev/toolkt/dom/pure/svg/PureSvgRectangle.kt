package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.Size
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGRectElement

data class PureSvgRectangle(
    val position: Point,
    val size: Size,
    override val stroke: Stroke? = Stroke.default,
    override val fill: Fill.Specified? = Fill.Specified.default,
) : PureSvgShape() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("rect").apply {
        setAttribute("x", position.x.toString())
        setAttribute("y", position.y.toString())
        setAttribute("width", size.width.value.toString())
        setAttribute("height", size.height.value.toString())

        setupRawShape(element = this)
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is PureSvgRectangle -> false
        !position.equalsWithTolerance(other.position, tolerance) -> false
        !size.equalsWithTolerance(other.size, tolerance) -> false
        !stroke.equalsWithToleranceOrNull(other.stroke, tolerance) -> false
        !fill.equalsWithToleranceOrNull(other.fill, tolerance) -> false
        else -> true
    }

    override fun transformVia(
        transformation: Transformation,
    ): PureSvgRectangle {
        val projection = transformation.toProjection
            ?: throw UnsupportedOperationException("SvgRectangle does not support this transformation: $transformation")

        return PureSvgRectangle(
            position = position.transformBy(projection),
            size = size.scaleBy(projection.scaling),
        )
    }

    fun contains(point: Point): Boolean {
        val xMin = position.x
        val xMax = xMin + size.width.value
        val yMin = position.y
        val yMax = yMin + size.height.value

        return point.x in xMin..xMax && point.y in yMin..yMax
    }
}

fun SVGRectElement.toSimpleRect(): PureSvgRectangle = PureSvgRectangle(
    position = Point(
        x = x.baseVal.value.toDouble(),
        y = y.baseVal.value.toDouble(),
    ),
    size = Size(
        width = width.baseVal.value.toDouble(),
        height = height.baseVal.value.toDouble(),
    ),
    stroke = extractStroke(),
)

