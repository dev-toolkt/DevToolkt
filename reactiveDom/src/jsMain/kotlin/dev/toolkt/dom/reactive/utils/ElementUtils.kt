package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle

fun Document.createReactiveElement(
    namespace: String? = null,
    /**
     * A name of a styleable element
     */
    name: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): Element {
    val element = when {
        namespace != null -> this.createElementNS(
            namespace = namespace,
            qualifiedName = name,
        )

        else -> this.createElement(
            localName = name,
        )

    }

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") (element as ElementCSSInlineStyle)

    style?.bind(
        styleDeclaration = element.style,
    )

    children?.bind(
        target = element,
        extract = Node::childNodesList,
    )

    return element
}
