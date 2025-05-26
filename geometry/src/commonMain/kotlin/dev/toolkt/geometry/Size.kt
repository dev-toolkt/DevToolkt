package dev.toolkt.geometry

import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.core.numeric.NumericObject

/**
 * Size of a two-dimensional object
 */
data class Size(
    val width: Span,
    val height: Span,
) : SpatialObject, NumericObject {
    constructor(
        width: Double,
        height: Double,
    ) : this(
        width = Span.Companion.of(value = width),
        height = Span.Companion.of(value = height),
    )

    override fun equalsWithSpatialTolerance(
        other: SpatialObject,
        tolerance: SpatialObject.SpatialTolerance,
    ): Boolean = when {
        other !is Size -> false
        !width.equalsWithSpatialTolerance(other.width, tolerance = tolerance) -> false
        !height.equalsWithSpatialTolerance(other.height, tolerance = tolerance) -> false
        else -> true
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean {
        TODO()
    }

    fun scaleBy(
        scaling: PrimitiveTransformation.Scaling,
    ): Size = Size(
        width = width * scaling.scaleVector.x,
        height = height * scaling.scaleVector.y,
    )
}
