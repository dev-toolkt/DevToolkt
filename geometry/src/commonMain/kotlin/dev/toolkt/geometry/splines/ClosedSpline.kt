package dev.toolkt.geometry.splines

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.geometry.LineSegment
import dev.toolkt.geometry.curves.OpenCurve
import dev.toolkt.geometry.curves.PrimitiveCurve
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.indentLater
import dev.toolkt.core.toReprString
import dev.toolkt.core.iterable.clusterSimilarConsecutive
import dev.toolkt.core.iterable.withNextCyclic
import dev.toolkt.core.iterable.withPreviousCyclic

/**
 * A composite closed curve guaranteed only to be positionally-continuous (C0).
 */
data class ClosedSpline(
    val cyclicLinks: List<Spline.Link>,
) : Spline, NumericObject {
    companion object {
        fun positionallyContinuous(
            links: List<Spline.Link>,
        ): ClosedSpline = ClosedSpline(
            cyclicLinks = links,
        )

        /**
         * @param cyclicCurves a list of curves, where each curves starts at
         * the end of the previous one, and ends at the start of the next one
         * (cyclically).
         */
        fun connect(
            cyclicCurves: List<OpenCurve>,
        ): ClosedSpline {
            val primitiveCyclicCurves = cyclicCurves.flatMap { it.subCurves }

            return ClosedSpline(
                cyclicLinks = primitiveCyclicCurves.withPreviousCyclic().map { (prevCurve, curve) ->
                    Spline.Link.connect(
                        prevCurve = prevCurve,
                        curve = curve,
                    )
                },
            )
        }

        /**
         * @param separatedCurves a list of curves, where each curve might be
         * separated from (or even intersect) the previous/next curve.
         */
        fun interconnect(
            separatedCurves: List<OpenCurve>,
        ): ClosedSpline = ClosedSpline.connect(
            cyclicCurves = separatedCurves.withNextCyclic().flatMap { (curve, nextCurve) ->
                listOf(curve) + LineSegment(
                    start = curve.end,
                    end = nextCurve.start,
                )
            },
        )
    }

    init {
        require(cyclicLinks.isNotEmpty())
    }

    val cyclicCurves: List<PrimitiveCurve>
        get() = cyclicLinks.withPreviousCyclic().map { (prevLink, link) ->
            link.bind(
                start = prevLink.end,
            )
        }

    val smoothSubSplines: List<OpenSpline>
        get() = cyclicCurves.clusterSimilarConsecutive { prevCurve, nextCurve ->
            prevCurve.connectsSmoothly(
                nextCurve = nextCurve,
            )
        }.map { smoothSequentialCurves ->
            OpenSpline.connect(
                sequentialCurves = smoothSequentialCurves,
            )
        }

    fun transformBy(
        transformation: Transformation,
    ): ClosedSpline = ClosedSpline(
        cyclicLinks = cyclicLinks.map {
            it.transformBy(transformation = transformation)
        },
    )

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is ClosedSpline -> false
        !cyclicLinks.equalsWithTolerance(other.cyclicLinks, tolerance) -> false
        else -> true
    }

    override val links: List<Spline.Link>
        get() = cyclicLinks

    override val segmentCurves: List<PrimitiveCurve>
        get() = cyclicCurves

    override fun toReprString(): String {
        return """
            |ClosedSpline(
            |  cyclicLinks = ${cyclicLinks.toReprString().indentLater()},
            |)
        """.trimMargin()
    }
}
