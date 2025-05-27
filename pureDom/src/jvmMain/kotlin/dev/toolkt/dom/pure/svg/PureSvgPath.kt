package dev.toolkt.dom.pure.svg

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.PrimitiveTransformation
import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import dev.toolkt.core.iterable.mapCarrying
import dev.toolkt.core.iterable.uncons
import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.utils.xml.svg.asList
import dev.toolkt.dom.pure.utils.xml.svg.getComputedStyle
import dev.toolkt.dom.pure.utils.xml.svg.toList
import dev.toolkt.dom.pure.utils.xml.svg.toSimpleColor
import org.apache.batik.css.engine.SVGCSSEngine
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegCurvetoCubicRel
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticAbs
import org.w3c.dom.svg.SVGPathSegCurvetoQuadraticRel
import org.w3c.dom.svg.SVGPathSegMovetoAbs

data class PureSvgPath(
    override val stroke: Stroke? = Stroke.default,
    override val fill: Fill? = Fill.None,
    override val markerEndId: String? = null,
    val segments: List<Segment>,
) : PureSvgShape() {
    sealed class Segment : NumericObject {
        data object ClosePath : Segment() {
            override val finalPointOrNull: Nothing?
                get() = null

            override fun toPathSegString(): String = "Z"

            override fun transformVia(
                transformation: Transformation,
            ): Segment = this

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = other == ClosePath
        }

        sealed class ActiveSegment : Segment() {
            final override val finalPointOrNull: Point
                get() = finalPoint

            abstract val finalPoint: Point

            val effectiveReflectedControlPoint: Point
                get() = when (this) {
                    is CubicBezierSegment -> controlPoint2.reflectedBy(finalPoint)
                    else -> finalPoint
                }
        }

        sealed class CurveSegment : ActiveSegment()

        data class MoveTo(
            val targetPoint: Point,
        ) : ActiveSegment() {
            override val finalPoint: Point
                get() = targetPoint

            override fun toPathSegString(): String = "M${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when (other) {
                is MoveTo -> targetPoint.equalsWithTolerance(other.targetPoint, tolerance)
                else -> false
            }

            override fun transformVia(
                transformation: Transformation,
            ): MoveTo = MoveTo(
                targetPoint = transformation.transform(point = targetPoint),
            )
        }

        data class LineTo(
            override val finalPoint: Point,
        ) : CurveSegment() {
            override fun toPathSegString(): String = "L${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when (other) {
                is LineTo -> finalPoint.equalsWithTolerance(other.finalPoint, tolerance)
                else -> false
            }

            override fun transformVia(
                transformation: Transformation,
            ): LineTo = LineTo(
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        sealed class QuadraticBezierSegment : CurveSegment()

        data class QuadraticBezierCurveTo(
            val controlPoint: Point,
            override val finalPoint: Point,
        ) : QuadraticBezierSegment() {
            override fun toPathSegString(): String = "Q${controlPoint.toSvgString()} ${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericObject.Tolerance,
            ): Boolean = when {
                other !is QuadraticBezierCurveTo -> false
                !controlPoint.equalsWithTolerance(other.controlPoint, tolerance) -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): QuadraticBezierCurveTo = QuadraticBezierCurveTo(
                controlPoint = transformation.transform(point = controlPoint),
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        data class SmoothQuadraticBezierCurveTo(
            override val finalPoint: Point,
        ) : QuadraticBezierSegment() {
            override fun toPathSegString(): String = "T${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericObject.Tolerance,
            ): Boolean = when {
                other !is SmoothQuadraticBezierCurveTo -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): SmoothQuadraticBezierCurveTo = SmoothQuadraticBezierCurveTo(
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        sealed class CubicBezierSegment : CurveSegment() {
            abstract val controlPoint2: Point
        }

        data class CubicBezierCurveTo(
            val controlPoint1: Point,
            override val controlPoint2: Point,
            override val finalPoint: Point,
        ) : CubicBezierSegment() {
            override fun toPathSegString(): String =
                "C${controlPoint1.toSvgString()} ${controlPoint2.toSvgString()} ${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when {
                other !is CubicBezierCurveTo -> false
                !controlPoint1.equalsWithTolerance(other.controlPoint1, tolerance) -> false
                !controlPoint2.equalsWithTolerance(other.controlPoint2, tolerance) -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): CubicBezierCurveTo = CubicBezierCurveTo(
                controlPoint1 = transformation.transform(point = controlPoint1),
                controlPoint2 = transformation.transform(point = controlPoint2),
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        data class SmoothCubicBezierCurveTo(
            override val controlPoint2: Point,
            override val finalPoint: Point,
        ) : CubicBezierSegment() {
            override fun toPathSegString(): String = "S${controlPoint2.toSvgString()} ${finalPoint.toSvgString()}"

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when {
                other !is SmoothCubicBezierCurveTo -> false
                !controlPoint2.equalsWithTolerance(other.controlPoint2, tolerance) -> false
                !finalPoint.equalsWithTolerance(other.finalPoint, tolerance) -> false
                else -> true
            }

            override fun transformVia(
                transformation: Transformation,
            ): SmoothCubicBezierCurveTo = SmoothCubicBezierCurveTo(
                controlPoint2 = transformation.transform(point = controlPoint2),
                finalPoint = transformation.transform(point = finalPoint),
            )
        }

        abstract val finalPointOrNull: Point?

        abstract fun toPathSegString(): String

        abstract fun transformVia(
            transformation: Transformation,
        ): Segment

        protected fun Point.toSvgString(): String = "${x},${y}"
    }

    companion object {
        fun polyline(
            stroke: Stroke,
            points: List<Point>,
        ): PureSvgPath? {
            val (firstPoint, trailingPoints) = points.uncons() ?: return null

            return PureSvgPath(
                stroke = stroke,
                segments = listOf(
                    Segment.MoveTo(
                        targetPoint = firstPoint,
                    ),
                ) + trailingPoints.map { point ->
                    Segment.LineTo(
                        finalPoint = point,
                    )
                },
            )
        }
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createSvgElement("path").apply {
        setAttribute("d", segments.joinToString(" ") { it.toPathSegString() })

        setupRawShape(element = this)
    }

    override fun equalsWithTolerance(
        other: NumericObject, tolerance: NumericObject.Tolerance
    ): Boolean = when {
        other !is PureSvgPath -> false
        !stroke.equalsWithToleranceOrNull(other.stroke, tolerance) -> false
        !segments.equalsWithTolerance(other.segments, tolerance) -> false
        else -> true
    }

    override fun transformVia(
        transformation: Transformation,
    ): PureSvgPath = PureSvgPath(
        stroke = stroke,
        segments = segments.map { segment ->
            segment.transformVia(transformation = transformation)
        },
    )
}

fun SVGPathElement.toSimplePath(): PureSvgPath {
    val (segments, _) = pathSegList.asList().mapCarrying(
        initialCarry = Point.origin,
    ) { currentPoint, svgPathSeg ->
        val segment = svgPathSeg.toSimpleSegment(currentPoint = currentPoint)

        Pair(
            segment,
            segment.finalPointOrNull ?: currentPoint,
        )
    }

    return PureSvgPath(
        stroke = extractStroke(),
        segments = segments,
    )
}

fun SVGElement.extractStroke(): PureSvgShape.Stroke {
    val strokeColor = getComputedStyle(SVGCSSEngine.STROKE_INDEX).toSimpleColor()
    val strokeWidth = getComputedStyle(SVGCSSEngine.STROKE_WIDTH_INDEX).floatValue.toDouble()
    val strokeDashArray = getComputedStyle(SVGCSSEngine.STROKE_DASHARRAY_INDEX).toList()

    return PureSvgShape.Stroke(
        color = strokeColor ?: PureColor.Companion.black,
        width = strokeWidth,
        dashArray = strokeDashArray?.map { it.floatValue.toDouble() },
    )
}

fun SVGPathSeg.toSimpleSegment(
    currentPoint: Point,
): PureSvgPath.Segment = when (pathSegType) {
    SVGPathSeg.PATHSEG_MOVETO_ABS -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.MoveTo(
            targetPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_MOVETO_REL -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.MoveTo(
            targetPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_LINETO_ABS -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.LineTo(
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_LINETO_REL -> {
        this as SVGPathSegMovetoAbs

        PureSvgPath.Segment.LineTo(
            finalPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_ABS -> {
        this as SVGPathSegCurvetoQuadraticAbs

        PureSvgPath.Segment.QuadraticBezierCurveTo(
            controlPoint = Point(
                x = x1.toDouble(),
                y = y1.toDouble(),
            ),
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_QUADRATIC_REL -> {
        this as SVGPathSegCurvetoQuadraticRel

        PureSvgPath.Segment.QuadraticBezierCurveTo(
            controlPoint = PrimitiveTransformation.Translation(
                tx = x1.toDouble(),
                ty = y1.toDouble(),
            ).transform(
                point = currentPoint,
            ),
            finalPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> {
        this as SVGPathSegCurvetoCubicAbs

        PureSvgPath.Segment.CubicBezierCurveTo(
            controlPoint1 = Point(
                x = x1.toDouble(),
                y = y1.toDouble(),
            ),
            controlPoint2 = Point(
                x = x2.toDouble(),
                y = y2.toDouble(),
            ),
            finalPoint = Point(
                x = x.toDouble(),
                y = y.toDouble(),
            ),
        )
    }

    SVGPathSeg.PATHSEG_CURVETO_CUBIC_REL -> {
        this as SVGPathSegCurvetoCubicRel

        PureSvgPath.Segment.CubicBezierCurveTo(
            controlPoint1 = PrimitiveTransformation.Translation(
                tx = x1.toDouble(),
                ty = y1.toDouble(),
            ).transform(
                point = currentPoint,
            ),
            controlPoint2 = PrimitiveTransformation.Translation(
                tx = x2.toDouble(),
                ty = y2.toDouble(),
            ).transform(
                point = currentPoint,
            ),
            finalPoint = PrimitiveTransformation.Translation(
                tx = x.toDouble(),
                ty = y.toDouble(),
            ).transform(
                point = currentPoint,
            ),
        )
    }

    SVGPathSeg.PATHSEG_CLOSEPATH -> PureSvgPath.Segment.ClosePath

    else -> error("Unsupported path segment type: $pathSegType (${this.pathSegTypeAsLetter})")
}
