package dev.toolkt.dom.reactive.utils

import dev.toolkt.dom.pure.input.PureInputType
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Node
import org.w3c.dom.events.Event

// This is a type hack
abstract external class HTMLTypedInputElement<T : PureInputType> : HTMLInputElement

typealias HTMLCheckboxElement = HTMLTypedInputElement<PureInputType.Checkbox>

fun <T : PureInputType> Document.createReactiveHtmlInputElement(
    type: T,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): HTMLTypedInputElement<T> {
    val inputElement = createReactiveHtmlElement(
        localName = "input",
        style = style,
        children = children,
    ) as HTMLInputElement

    inputElement.type = type.type

    return inputElement.unsafeCast<HTMLTypedInputElement<T>>()
}

fun HTMLInputElement.getChangeEventStream(): EventStream<Event> = this.getEventStream(
    type = "change"
)

fun HTMLCheckboxElement.getCheckedEventStream() = this.getChangeEventStream().map {
    val target = it.target as HTMLInputElement
    target.checked
}

fun HTMLCheckboxElement.getCheckedCell(): Cell<Boolean> = getCheckedEventStream().hold(initialValue = checked)
