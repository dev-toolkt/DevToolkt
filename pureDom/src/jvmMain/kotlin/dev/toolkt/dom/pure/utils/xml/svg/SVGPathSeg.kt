package dev.toolkt.dom.pure.utils.xml.svg

import dev.toolkt.geometry.Point
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegCurvetoCubicAbs
import org.w3c.dom.svg.SVGPathSegLinetoAbs
import org.w3c.dom.svg.SVGPathSegMovetoAbs

val SVGPathSeg.asSVGPathSegMovetoAbs: SVGPathSegMovetoAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_MOVETO_ABS -> this as SVGPathSegMovetoAbs
        else -> null
    }

val SVGPathSeg.asSVGPathSegLineToAbs: SVGPathSegLinetoAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_LINETO_ABS -> this as SVGPathSegLinetoAbs
        else -> null
    }

val SVGPathSeg.asSVGPathSegCurveToCubicAbs: SVGPathSegCurvetoCubicAbs?
    get() = when {
        pathSegType == SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> this as SVGPathSegCurvetoCubicAbs
        else -> null
    }

fun <R> SVGPathSeg.match(
    moveToAbs: (SVGPathSegMovetoAbs) -> R,
    lineToAbs: (SVGPathSegLinetoAbs) -> R,
    curveToCubicAbs: (SVGPathSegCurvetoCubicAbs) -> R,
): R = when (pathSegType) {
    SVGPathSeg.PATHSEG_MOVETO_ABS -> moveToAbs(this as SVGPathSegMovetoAbs)
    SVGPathSeg.PATHSEG_LINETO_ABS -> lineToAbs(this as SVGPathSegLinetoAbs)
    SVGPathSeg.PATHSEG_CURVETO_CUBIC_ABS -> curveToCubicAbs(this as SVGPathSegCurvetoCubicAbs)
    else -> throw UnsupportedOperationException("Unsupported path segment type: $pathSegType (${pathSegTypeAsLetter})")
}

/**
 * The target point of the MoveTo path segment.
 */
val SVGPathSegMovetoAbs.p: Point
    get() = Point(
        x.toDouble(),
        y.toDouble(),
    )

/**
 * The end point of the LineTo path segment.
 */
val SVGPathSegLinetoAbs.p: Point
    get() = Point(
        x.toDouble(),
        y.toDouble(),
    )

/**
 * The first control point of the cubic Bézier curve.
 */
val SVGPathSegCurvetoCubicAbs.p1: Point
    get() = Point(
        x1.toDouble(),
        y1.toDouble(),
    )

/**
 * The second control point of the cubic Bézier curve.
 */
val SVGPathSegCurvetoCubicAbs.p2: Point
    get() = Point(
        x2.toDouble(),
        y2.toDouble(),
    )

/**
 * The end point of the cubic Bézier curve.
 */
val SVGPathSegCurvetoCubicAbs.p: Point
    get() = Point(
        x.toDouble(),
        y.toDouble(),
    )
