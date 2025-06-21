package svgPathData

import org.w3c.dom.svg.SVGPathElement

@Suppress("NOTHING_TO_INLINE")
inline fun SVGPathSegment(
    type: String,
    values: Array<Number> = emptyArray(),
): SVGPathSegment {
    val o = js("({})")
    o["type"] = type
    o["values"] = values
    return o
}

@Suppress("NOTHING_TO_INLINE")
inline fun SVGPathDataSettings(
    normalize: Boolean = false,
): SVGPathDataSettings {
    val o = js("({})")
    o["normalize"] = normalize
    return o
}

fun SVGPathElement.getPathData(
    settings: SVGPathDataSettings? = null,
): Array<SVGPathSegment> = asDynamic().getPathData(settings)

fun SVGPathElement.setPathData(
    pathData: Array<SVGPathSegment>,
) {
    asDynamic().setPathData(pathData)
}
