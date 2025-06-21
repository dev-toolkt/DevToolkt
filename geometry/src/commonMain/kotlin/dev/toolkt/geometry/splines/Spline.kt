package dev.toolkt.geometry.splines

import dev.toolkt.core.ReprObject
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.indentLater

/**
 * A composite curve, at least positionally-continuous (C0), either open or
 * closed.
 */
interface Spline : NumericObject, ReprObject {
    data class Link(
        val edge: PrimitiveCurve.Edge,
        val end: Point,
    ) : NumericObject, ReprObject {
        companion object {
            fun connect(
                prevCurve: PrimitiveCurve,
                curve: PrimitiveCurve,
            ): Link {
                if (prevCurve.end != curve.start) {
                    throw IllegalArgumentException("The curves are not sequential: ${prevCurve.end} != ${curve.start}")
                }

                return Spline.Link(
                    edge = curve.edge,
                    end = curve.end,
                )
            }
        }

        fun bind(
            start: Point,
        ): PrimitiveCurve = edge.bind(
            start = start,
            end = end,
        )

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean = when {
            other !is Link -> false
            !edge.equalsWithTolerance(other.edge, tolerance) -> false
            !end.equalsWithTolerance(other.end, tolerance) -> false
            else -> true
        }

        fun transformBy(
            transformation: Transformation,
        ): Link = Link(
            edge = edge.transformBy(transformation = transformation),
            end = end.transformBy(transformation = transformation),
        )

        override fun toReprString(): String {
            return """
                |Spline.Link(
                |  edge = ${edge.toReprString().indentLater()},
                |  end = ${end.toReprString()},
                |)
             """.trimMargin()
        }
    }

    companion object;

    val links: List<Link>

    val segmentCurves: List<PrimitiveCurve>
}
