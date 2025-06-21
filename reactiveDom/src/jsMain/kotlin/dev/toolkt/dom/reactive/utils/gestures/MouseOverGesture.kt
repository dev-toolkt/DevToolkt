package dev.toolkt.dom.reactive.utils.gestures

import dev.toolkt.dom.reactive.utils.event.clientPoint
import dev.toolkt.dom.reactive.utils.event.offsetPoint
import dev.toolkt.dom.reactive.utils.html.getMouseEnterEventStream
import dev.toolkt.dom.reactive.utils.html.getMouseLeaveEventStream
import dev.toolkt.dom.reactive.utils.html.getMouseMoveEventStream
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.future.Future
import org.w3c.dom.Element
import org.w3c.dom.events.MouseEvent

class MouseOverGesture(
    private val newestMouseEvent: Cell<MouseEvent>,
    val onFinished: Future<Unit>,
) {
    val clientPosition: Cell<Point>
        get() = newestMouseEvent.map { it.clientPoint }

    val offsetPosition: Cell<Point>
        get() = newestMouseEvent.map { it.offsetPoint }
}

fun Element.onMouseOverGestureStarted(): EventStream<MouseOverGesture> =
    this.getMouseEnterEventStream().map { mouseEnterEvent ->
        MouseOverGesture(
            newestMouseEvent = getMouseMoveEventStream().hold(mouseEnterEvent),
            onFinished = getMouseLeaveEventStream().next().unit(),
        )
    }

fun Element.trackMouseOverGesture(): Cell<MouseOverGesture?> = Future.oscillate(
    initialValue = null,
    switchPhase1 = { this.onMouseOverGestureStarted().next() },
    switchPhase2 = { gesture -> gesture.onFinished.null_() },
)
