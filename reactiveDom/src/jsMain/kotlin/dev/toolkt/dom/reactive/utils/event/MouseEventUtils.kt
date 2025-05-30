package dev.toolkt.dom.reactive.utils.event

import dev.toolkt.geometry.Point
import org.w3c.dom.events.MouseEvent

val MouseEvent.clientPoint: Point
    get() = Point(
        x = clientX.toDouble(),
        y = clientY.toDouble(),
    )
