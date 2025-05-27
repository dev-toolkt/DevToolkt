package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import org.w3c.dom.Document
import org.w3c.dom.Element

data class PureSvgCircle(
    val center: Point,
    val radius: Double,
    override val stroke: Stroke? = Stroke.default,
    override val fill: Fill.Specified? = Fill.Specified.default,
) : PureSvgShape() {
    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("circle").apply {
        setAttribute("cx", center.x.toString())
        setAttribute("cy", center.y.toString())
        setAttribute("r", radius.toString())

        setupRawShape(element = this)
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is PureSvgCircle -> false
        !center.equalsWithTolerance(other.center, tolerance) -> false
        !radius.equalsWithTolerance(other.radius, tolerance) -> false
        !stroke.equalsWithToleranceOrNull(other.stroke, tolerance) -> false
        !fill.equalsWithToleranceOrNull(other.fill, tolerance) -> false
        else -> true
    }

    override fun transformVia(
        transformation: Transformation,
    ): PureSvgShape = PureSvgCircle(
        center = transformation.transform(point = center),
        radius = radius,
        stroke = stroke,
        fill = fill,
    )
}
