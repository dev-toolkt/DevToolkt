package dev.toolkt.dom.reactive.html

import dev.toolkt.geometry.Point
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent

data class HtmlMouseEvent(
    val position: Point,
) : HtmlEvent() {
    companion object : Wrapper<HtmlMouseEvent> {
        override fun wrap(rawEvent: Event): HtmlMouseEvent {
            val mouseEvent = rawEvent as MouseEvent

            return HtmlMouseEvent(
                position = Point(
                    x = mouseEvent.x,
                    y = mouseEvent.y,
                )
            )
        }
    }
}
