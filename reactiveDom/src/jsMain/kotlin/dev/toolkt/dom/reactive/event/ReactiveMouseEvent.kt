package dev.toolkt.dom.reactive.event

import dev.toolkt.geometry.Point
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

data class ReactiveMouseEvent(
    val position: Point,
) : ReactiveEvent() {
    companion object : Wrapper<ReactiveMouseEvent> {
        override fun wrap(rawEvent: Event): ReactiveMouseEvent {
            val mouseEvent = rawEvent as MouseEvent

            return ReactiveMouseEvent(
                position = Point(
                    x = mouseEvent.x,
                    y = mouseEvent.y,
                )
            )
        }
    }
}
