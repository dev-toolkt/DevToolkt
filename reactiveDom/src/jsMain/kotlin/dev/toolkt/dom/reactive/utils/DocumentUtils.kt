package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.Node
import org.w3c.dom.Text
import org.w3c.dom.css.ElementCSSInlineStyle

fun Document.createReactiveTextNode(
    data: Cell<String>,
): Text = data.formAndForget(
    create = { initialValue: String ->
        document.createTextNode(
            data = initialValue,
        )
    },
    update = { textNode: Text, newValue: String ->
        textNode.data = newValue
    },
)

fun Document.createReactiveHtmlElement(
    /**
     * A name of a styleable element
     */
    localName: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): Element {
    val element = this.createElement(
        localName,
    )

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

fun Document.createReactiveHtmlInputElement(
    type: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLInputElement {
    val inputElement = createReactiveHtmlElement(
        localName = "input",
        style = style,
        children = children,
    ) as HTMLInputElement

    inputElement.type = type

    return inputElement
}

fun Document.createReactiveHtmlButtonElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLButtonElement = createReactiveHtmlElement(
    localName = "button",
    style = style,
    children = children,
) as HTMLButtonElement

fun Document.createReactiveHtmlDivElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLDivElement = createReactiveHtmlElement(
    localName = "div",
    style = style,
    children = children,
) as HTMLDivElement

fun Document.createReactiveHtmlSpanElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLSpanElement = createReactiveHtmlElement(
    localName = "span",
    style = style,
    children = children,
) as HTMLSpanElement
