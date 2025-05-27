package dev.toolkt.dom.reactive.node.element

import dev.toolkt.dom.reactive.event.ReactiveEvent
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveInputChangeEvent
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import org.w3c.dom.Element
import org.w3c.dom.HTMLInputElement

abstract class ReactiveInputElement<ChangeEventT : ReactiveInputChangeEvent>(
    style: ReactiveStyle?,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent>?,
    private val handleChange: ReactiveEventHandler<ChangeEventT> = ReactiveEventHandler.Accepting,
) : ReactiveHtmlElement(
    style = style,
    handleMouseDown = handleMouseDown,
) {
    final override val elementName: String = "input"

    protected val rawInputElement: HTMLInputElement
        get() = rawElement as HTMLInputElement

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
