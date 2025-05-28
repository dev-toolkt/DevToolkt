package dev.toolkt.dom.reactive.node.element

import dev.toolkt.dom.pure.collections.ChildNodesDomList
import dev.toolkt.dom.pure.collections.MutableDomList
import dev.toolkt.dom.pure.collections.childNodesList
import dev.toolkt.dom.reactive.event.ReactiveEventHandler
import dev.toolkt.dom.reactive.event.ReactiveMouseEvent
import dev.toolkt.dom.reactive.event.attach
import dev.toolkt.dom.reactive.node.ReactiveNode
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.reactive.event_stream.EventEmitter
import dev.toolkt.reactive.event_stream.EventStream
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.css.ElementCSSInlineStyle

abstract class ReactiveHtmlElement(
    style: ReactiveStyle?,
    handleMouseDown: ReactiveEventHandler<ReactiveMouseEvent>?,
) : ReactiveElement() {
    companion object {
        private fun bindChildren(
            target: Node,
            children: ReactiveList<ReactiveNode>,
        ) {
            children.map {
                it.rawNode
            }.bind(
                target = target,
                extract = ::ChildNodesDomList,
            )
        }
    }

    private val onMouseDownEmitter = EventEmitter<ReactiveMouseEvent>()

    val onMouseDown: EventStream<ReactiveMouseEvent>
        get() = onMouseDownEmitter

    override val rawElement: Element by lazy {
        document.createReactiveHtmlElement(
            localName = elementName,
            style = style,
            children = children.map { it.rawNode },
        ).also { element ->
            setupElement(
                element = element,
            )

            handleMouseDown?.attach(
                target = element,
                eventName = "mouseDown",
                wrapper = ReactiveMouseEvent.Companion,
                emitter = onMouseDownEmitter,
            )
        }
    }

    abstract val elementName: String

    abstract val children: ReactiveList<ReactiveNode>

    protected abstract fun setupElement(
        element: Element,
    )
}

fun Document.createReactiveHtmlElement(
    /**
     * A name of a styleable element
     */
    localName: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): Element {
    val element = this.createElement(
        localName,
    )

    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE") (element as ElementCSSInlineStyle)

    style?.bind(
        styleDeclaration = element.style,
    )

    children?.bind(
        target = element,
        extract = Node::childNodesList,
    )

    return element
}
