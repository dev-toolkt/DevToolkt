package dev.toolkt.dom.reactive.html

import org.w3c.dom.events.Event

sealed class HtmlEvent {
    interface Wrapper<E : HtmlEvent> {
        fun wrap(rawEvent: Event): E
    }
}
