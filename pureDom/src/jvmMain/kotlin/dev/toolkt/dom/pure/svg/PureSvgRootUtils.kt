package dev.toolkt.dom.pure.svg

import dev.toolkt.dom.pure.utils.xml.svg.MinimalCssContext
import dev.toolkt.dom.pure.utils.xml.svg.SVGDOMImplementationUtils
import dev.toolkt.dom.pure.utils.xml.svg.createSvgDocument
import dev.toolkt.dom.pure.utils.xml.svg.documentSvgElement
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.apache.batik.anim.dom.SVGOMDocument
import org.w3c.dom.svg.SVGDocument
import java.io.Reader

private val svgDocumentFactory = SAXSVGDocumentFactory(null)

private val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementationUtils.getSVGDOMImplementation()

fun PureSvgRoot.Companion.parse(
    reader: Reader,
): PureSvgRoot {
    val uri = "file://Document.svg"

    val document = svgDocumentFactory.createDocument(uri, reader) as SVGOMDocument

    document.cssEngine = svgDomImplementation.createCSSEngine(document, MinimalCssContext())

    return document.toSimple()
}

fun PureSvgRoot.toSvgDocument(): SVGDocument = svgDomImplementation.createSvgDocument().apply {
    setup(
        document = this,
        root = documentSvgElement,
    )
}
