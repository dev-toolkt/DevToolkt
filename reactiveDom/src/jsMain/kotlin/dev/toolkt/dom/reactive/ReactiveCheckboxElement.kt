package dev.toolkt.dom.reactive

import dev.toolkt.dom.reactive.event.ReactiveCheckboxChangeEvent
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.event_stream.hold
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.events.Event

class ReactiveCheckboxElement(
    override val children: ReactiveList<ReactiveNode> = ReactiveList.Empty,
    style: ReactiveStyle? = null,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent> = ReactiveEventHandler.Accepting,
) : ReactiveInputElement<ReactiveCheckboxChangeEvent>(
    style = style,
    handleMouseDown = handleMouseDown,
) {
    val isChecked: Cell<Boolean>
        get() = onChange.map {
            it.isChecked
        }.hold(
            initialValue = isCheckedNow,
        )

    val isCheckedNow: Boolean
        get() = rawInputElement.checked

    fun setChecked(
        state: Boolean,
    ) {
        if (isCheckedNow != state) {
            rawInputElement.checked = state

            rawInputElement.dispatchEvent(Event("change"))
        }
    }

    override val changeEventWrapper = ReactiveCheckboxChangeEvent

    init {
        rawElement
    }
}
