package dev.toolkt.dom.reactive.utils

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.getEventStream
import dev.toolkt.reactive.event_stream.hold
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun HTMLInputElement.getChangeEventStream(): EventStream<Event> = this.getEventStream(
    type = "change"
)

fun HTMLInputElement.getCheckedEventStream() = this.getChangeEventStream().map {
    val target = it.target as HTMLInputElement
    target.checked
}

fun HTMLInputElement.getCheckedCell(): Cell<Boolean> = getCheckedEventStream().hold(initialValue = checked)
