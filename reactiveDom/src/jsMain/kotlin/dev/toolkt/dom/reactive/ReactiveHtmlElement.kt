package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.collections.ChildNodesDomList
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle

abstract class ReactiveHtmlElement(
    style: ReactiveStyle? = null,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent>?,
) : ReactiveElement() {
    companion object {
        private fun bindChildren(
            target: Node,
            children: ReactiveList<ReactiveNode>,
        ) {
            children.map {
                it.rawNode
            }.pipe(
                target = target,
                mutableList = ChildNodesDomList(node = target),
            )
        }
    }

    private val onMouseDownEmitter = EventEmitter<ReactiveMouseEvent>()

    val onMouseDown: EventStream<ReactiveMouseEvent>
        get() = onMouseDownEmitter

    override val rawElement: Element by lazy {
        document.createElement(
            localName = elementName,
        ).also { element ->
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            element as ElementCSSInlineStyle

            setupElement(
                element = element,
            )

            style?.bind(
                styleDeclaration = element.style,
            )

            handleMouseDown?.attach(
                target = element,
                eventName = "mouseDown",
                wrapper = ReactiveMouseEvent.Companion,
                emitter = onMouseDownEmitter,
            )

            bindChildren(
                target = element,
                children = children,
            )
        }
    }

    abstract val elementName: String

    abstract val children: ReactiveList<ReactiveNode>

    protected abstract fun setupElement(
        element: Element,
    )
}
