package dev.toolkt.dom.pure.svg

import dev.toolkt.core.numeric.assertEqualsWithTolerance
import dev.toolkt.dom.pure.utils.xml.childElements
import dev.toolkt.dom.pure.utils.xml.svg.SVGDOMImplementationUtils
import dev.toolkt.dom.pure.utils.xml.svg.documentSvgElement
import dev.toolkt.dom.pure.utils.xml.svg.parseSvgDocument
import dev.toolkt.geometry.Point
import org.apache.batik.anim.dom.SAXSVGDocumentFactory
import org.apache.batik.anim.dom.SVGDOMImplementation
import org.w3c.dom.svg.SVGPathElement
import kotlin.test.Test

class SvgPathTests {
    companion object {
        private val svgDocumentFactory = SAXSVGDocumentFactory(null)

        private val svgDomImplementation: SVGDOMImplementation = SVGDOMImplementationUtils.getSVGDOMImplementation()

        private fun parseSvgPath(
            pathString: String,
        ): SVGPathElement {
            val documentString = """
                <svg xmlns="http://www.w3.org/2000/svg">
                    $pathString
                </svg>
            """.trimIndent()

            val svgDocument = svgDocumentFactory.parseSvgDocument(
                svgDomImplementation = svgDomImplementation,
                reader = documentString.reader(),
            )

            val pathElement = svgDocument.documentSvgElement.childElements.singleOrNull() as? SVGPathElement
                ?: throw IllegalStateException("Expected a single SVGPathElement, but found ${svgDocument.documentSvgElement.childElements.size}")

            return pathElement
        }
    }

    @Test
    fun testToSimplePath_basic() {
        val pathElement = parseSvgPath(
            pathString = """
                <path
                    d="M12.3 45.6 L78.9 12.3 C34.5 67.8, 90.1 23.4, 56.7 89.0 Z"
                    stroke="red"
                    stroke-width="2"
                    stroke-dasharray="5, 2"
                />
            """.trimIndent(),
        )

        val path = pathElement.toSimplePath()

        val expectedPath = PureSvgPath(
            stroke = PureSvgShape.Stroke(
                color = PureColor.Companion.red,
                width = 2.0,
                dashArray = listOf(5.0, 2.0),
            ),
            segments = listOf(
                PureSvgPath.Segment.MoveTo(
                    targetPoint = Point(x = 12.3, y = 45.6),
                ),
                PureSvgPath.Segment.LineTo(
                    finalPoint = Point(x = 78.9, y = 12.3),
                ),
                PureSvgPath.Segment.CubicBezierCurveTo(
                    controlPoint1 = Point(x = 34.5, y = 67.8),
                    controlPoint2 = Point(x = 90.1, y = 23.4),
                    finalPoint = Point(x = 56.7, y = 89.0),
                ),
                PureSvgPath.Segment.ClosePath,
            ),
        )

        assertEqualsWithTolerance(
            expected = expectedPath,
            actual = path,
        )
    }
}
