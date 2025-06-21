package dev.toolkt.dom.pure.utils.xml.svg

import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGOMDocument
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGGElement
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGPathSeg
import org.w3c.dom.svg.SVGPathSegList
import org.w3c.dom.svg.SVGRectElement
import java.io.Reader

object SVGDOMImplementationUtils {
    fun getSVGDOMImplementation() = SVGDOMImplementation.getDOMImplementation() as SVGDOMImplementation
}

fun SVGDOMImplementation.createSvgDocument(): SVGDocument {
    val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    return createDocument(svgNS, "svg", null) as SVGDocument
}

fun SAXSVGDocumentFactory.parseSvgDocument(
    svgDomImplementation: SVGDOMImplementation,
    reader: Reader,
): SVGDocument {
    val uri = "file://Document.svg"

    val document = createDocument(uri, reader) as SVGOMDocument

    document.cssEngine = svgDomImplementation.createCSSEngine(document, MinimalCssContext())

    return document
}

val SVGDocument.documentSvgElement: SVGElement
    get() = documentElement as SVGElement

fun SVGDocument.createSvgElement(qualifiedName: String): Element = createElementNS(
    SVGDOMImplementation.SVG_NAMESPACE_URI,
    qualifiedName,
)

fun SVGDocument.createPathElement(): SVGPathElement = createSvgElement("path") as SVGPathElement

fun SVGDocument.createGElement(): SVGGElement = createSvgElement("g") as SVGGElement

fun SVGDocument.createRectElement(): SVGRectElement = createSvgElement("rect") as SVGRectElement

fun SVGDocument.createCircleElement(): SVGCircleElement = createSvgElement("circle") as SVGCircleElement


fun SVGPathSegList.appendAllItems(
    items: Iterable<SVGPathSeg>,
) {
    items.forEach { appendItem(it) }
}
