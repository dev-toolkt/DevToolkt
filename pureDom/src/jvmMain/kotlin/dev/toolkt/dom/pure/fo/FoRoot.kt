package dev.toolkt.dom.pure.fo

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.svg.PureSvg
import org.apache.fop.apps.FopFactory
import org.apache.fop.apps.MimeConstants
import org.apache.fop.util.XMLConstants
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.File
import java.io.OutputStream
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.sax.SAXResult
import kotlin.io.path.outputStream

data class FoRoot(
    val pageWidth: PureDimension<*>,
    val pageHeight: PureDimension<*>,
    val blocks: List<FoSvgBlock>,
) : FoElement() {
    companion object {
        private const val XMLNS_NS = XMLConstants.XMLNS_NAMESPACE_URI

        private const val MASTER_NAME = "main"
    }

    private fun toDocument(): Document {
        val factory = DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
        }

        val builder = factory.newDocumentBuilder()

        return builder.newDocument().apply {
            appendChild(
                toRawElement(document = this),
            )
        }
    }

    fun writePdfToFile(
        pdfFilePath: Path,
    ) {
        pdfFilePath.outputStream().use { fileOutputStream ->
            writePdfToStream(outputStream = fileOutputStream)
        }
    }

    fun writePdfToStream(
        outputStream: OutputStream,
    ) {
        val fopFactory = FopFactory.newInstance(File(".").toURI())
        val transformerFactory = TransformerFactory.newInstance()
        val foUserAgent = fopFactory.newFOUserAgent()

        val transformer = transformerFactory.newTransformer()

        val fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream)

        transformer.transform(
            DOMSource(toDocument()),
            SAXResult(fop.defaultHandler),
        )
    }

    override fun toRawElement(
        document: Document,
    ): Element = document.createFoElement("root").apply {
        setAttributeNS(XMLNS_NS, "xmlns:svg", PureSvg.Namespace)

        appendChild(
            document.createFoElement("layout-master-set").apply {
                appendChild(
                    document.createFoElement("simple-page-master").apply {
                        setAttribute("master-name", MASTER_NAME)
                        setAttribute("page-width", pageWidth.toDimensionString())
                        setAttribute("page-height", pageHeight.toDimensionString())

                        appendChild(
                            document.createFoElement("region-body").apply {
                                setAttribute("margin", "0")
                            },
                        )
                    },
                )
            },
        )

        appendChild(
            document.createFoElement("page-sequence").apply {
                setAttribute("master-reference", MASTER_NAME)

                appendChild(
                    document.createFoElement("flow").apply {
                        setAttribute("flow-name", "xsl-region-body")

                        blocks.forEach { block ->
                            appendChild(block.toRawElement(document = document))
                        }
                    },
                )
            },
        )
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = when {
        other !is FoRoot -> false
        !pageWidth.equalsWithTolerance(other.pageWidth, tolerance) -> false
        !pageHeight.equalsWithTolerance(other.pageHeight, tolerance) -> false
        !blocks.equalsWithTolerance(other.blocks, tolerance) -> false
        else -> true
    }
}
