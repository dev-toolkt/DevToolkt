package dev.toolkt.dom.reactive.utils.html

import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLSpanElement
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle
import org.w3c.dom.events.MouseEvent

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


fun HTMLElement.getMouseDownEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mouseDown"
).cast()

fun HTMLElement.getClickEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "click"
).cast()
