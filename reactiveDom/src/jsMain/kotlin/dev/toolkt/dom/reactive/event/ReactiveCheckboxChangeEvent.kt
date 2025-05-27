package dev.toolkt.dom.reactive.event

import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event

data class ReactiveCheckboxChangeEvent(
    val isChecked: Boolean,
) : ReactiveInputChangeEvent() {
    companion object : Wrapper<ReactiveCheckboxChangeEvent> {
        override fun wrap(rawEvent: Event): ReactiveCheckboxChangeEvent {
            val target = rawEvent.target ?: throw AssertionError("Checkbox change event target is null")

            target as HTMLInputElement

            return ReactiveCheckboxChangeEvent(
                isChecked = target.checked,
            )
        }
    }
}
