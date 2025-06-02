package dev.toolkt.dom.reactive.utils.gestures

import dev.toolkt.dom.reactive.utils.event.clientPoint
import dev.toolkt.dom.reactive.utils.html.getMouseEnterEventStream
import dev.toolkt.dom.reactive.utils.html.getMouseLeaveEventStream
import dev.toolkt.dom.reactive.utils.html.getMouseMoveEventStream
import dev.toolkt.geometry.Point
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.future.Future
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.event_stream.hold
import org.w3c.dom.Element

data class MouseOverGesture(
    val clientPosition: Cell<Point>,
    val onFinished: Future<Unit>,
)

fun Element.onMouseOverGestureStarted(): EventStream<MouseOverGesture> =
    this.getMouseEnterEventStream().map { mouseEnterEvent ->
        val initialClientPoint = mouseEnterEvent.clientPoint
        val newClientPoints = getMouseMoveEventStream().map { it.clientPoint }

        MouseOverGesture(
            clientPosition = newClientPoints.hold(initialClientPoint),
            onFinished = getMouseLeaveEventStream().next().unit(),
        )
    }

fun Element.trackMouseOver(): Cell<MouseOverGesture?> = Future.oscillate(
    initialValue = null,
    switchPhase1 = { this.onMouseOverGestureStarted().next() },
    switchPhase2 = { gesture -> gesture.onFinished.null_() },
)
