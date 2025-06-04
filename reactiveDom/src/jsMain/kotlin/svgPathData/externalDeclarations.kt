@file:JsNonModule

/**
 * External declarations for [SVG PathData](https://developer.mozilla.org/en-US/docs/Web/API/SVGPathElement/getPathData),
 * which needs a polyfill on some browsers.
 */
package svgPathData

external interface SVGPathDataSettings {
    var normalize: Boolean
}

external interface SVGPathSegment {
    var type: String /* "A" | "a" | "C" | "c" | "H" | "h" | "L" | "l" | "M" | "m" | "Q" | "q" | "S" | "s" | "T" | "t" | "V" | "v" | "Z" | "z" */
    var values: Array<Number>
}
