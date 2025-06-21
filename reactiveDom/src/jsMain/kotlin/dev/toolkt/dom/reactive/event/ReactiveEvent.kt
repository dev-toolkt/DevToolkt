package dev.toolkt.dom.reactive.event

import org.w3c.dom.events.Event

sealed class ReactiveEvent {
    interface Wrapper<E : ReactiveEvent> {
        fun wrap(rawEvent: Event): E
    }
}
