package dev.toolkt.dom.reactive

import dev.toolkt.dom.reactive.event.ReactiveEvent
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveInputChangeEvent
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

abstract class ReactiveInputElement<ChangeEventT : ReactiveInputChangeEvent>(
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent>? = null,
    private val handleChange: ReactiveEventHandler<ChangeEventT> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    handleMouseDown = handleMouseDown,
) {
    final override val elementName: String = "input"

    private val onChangeEmitter = EventEmitter<ChangeEventT>()

    val onChange: EventStream<ChangeEventT>
        get() = onChangeEmitter

    final override fun setupElement(element: Element) {
        element as HTMLInputElement

        element.type = "checkbox"

        handleChange.attach(
            target = element,
            eventName = "change",
            wrapper = changeEventWrapper,
            emitter = onChangeEmitter,
        )
    }

    protected abstract val changeEventWrapper: ReactiveEvent.Wrapper<ChangeEventT>
}
