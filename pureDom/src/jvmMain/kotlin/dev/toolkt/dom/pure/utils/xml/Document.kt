package dev.toolkt.dom.pure.utils.xml

import org.w3c.dom.Document
import java.nio.file.Path
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

fun Document.writeToFile(filePath: Path) {
    val transformer = TransformerFactory.newInstance().newTransformer()

    val source = DOMSource(this)
    val result = StreamResult(filePath.toFile())

    transformer.transform(source, result);
}
