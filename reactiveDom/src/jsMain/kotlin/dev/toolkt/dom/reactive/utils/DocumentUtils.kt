package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.reactive.node.element.createReactiveHtmlElement
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.MouseEvent

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
