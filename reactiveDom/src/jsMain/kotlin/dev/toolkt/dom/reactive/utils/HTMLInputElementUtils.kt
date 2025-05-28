package dev.toolkt.dom.reactive.utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.getEventStream
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

fun HTMLInputElement.getChangeEventStream(): EventStream<Event> = this.getEventStream(
    type = "change"
)
