package dev.toolkt.dom.pure.utils.xml.svg

import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.css.engine.CSSStylableElement
import org.apache.batik.css.engine.value.Value
import org.w3c.dom.svg.SVGElement

var SVGElement.width: String
    get() = getAttribute("width")
    set(value) {
        setAttribute("width", value)
    }

var SVGElement.height: String
    get() = getAttribute("height")
    set(value) {
        setAttribute("height", value)
    }

/**
 * The SVG presentational fill attribute and the CSS fill property can be used
 * with the following SVG elements:
 *
 *     <circle>
 *     <ellipse>
 *     <path>
 *     <polygon>
 *     <polyline>
 *     <rect>
 *     <text>
 *     <textPath>
 *     <tref>
 *     <tspan>
 */
var SVGElement.fill: String
    get() = getAttribute("fill")
    set(value) {
        setAttribute("fill", value)
    }

/**
 * You can use this attribute with the following SVG elements:
 *
 *     <circle>
 *     <ellipse>
 *     <line>
 *     <path>
 *     <polygon>
 *     <polyline>
 *     <rect>
 *     <text>
 *     <textPath>
 *     <tref>
 *     <tspan>
 */
var SVGElement.stroke: String
    get() = getAttribute("stroke")
    set(value) {
        setAttribute("stroke", value)
    }

var SVGElement.viewBox: SVGViewBox
    get() = SVGViewBox.fromSvgString(getAttribute("viewBox"))
    set(value) {
        setAttribute("viewBox", value.toSvgString())
    }

fun SVGElement.getComputedStyle(propertyIndex: Int): Value {
    val document = this.ownerDocument as SVGOMDocument
    val value = document.cssEngine.getComputedStyle(this as CSSStylableElement, null, propertyIndex)
    return value
}

