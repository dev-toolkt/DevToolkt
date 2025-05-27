package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.collections.ChildNodesDomList
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.EventTarget

abstract class ReactiveHtmlElement() : ReactiveElement() {
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


    override val rawElement: Element by lazy {
        document.createElement(
            localName = elementName,
        ).also { element ->
            attachEventHandlers(
                target = element,
            )

            bindChildren(
                target = element,
                children = children,
            )
        }
    }

    abstract val elementName: String

    abstract val children: ReactiveList<ReactiveNode>

    protected abstract fun attachEventHandlers(
        target: EventTarget,
    )
}