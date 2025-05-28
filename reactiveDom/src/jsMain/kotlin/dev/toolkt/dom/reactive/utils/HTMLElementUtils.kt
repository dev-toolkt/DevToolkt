package dev.toolkt.dom.reactive.utils

import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.cast
import dev.toolkt.reactive.event_stream.getEventStream
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent

fun HTMLElement.getMouseDownEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "mouseDown"
).cast()

fun HTMLElement.getClickEventStream(): EventStream<MouseEvent> = this.getEventStream(
    type = "click"
).cast()
