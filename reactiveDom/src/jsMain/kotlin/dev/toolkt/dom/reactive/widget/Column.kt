package dev.toolkt.dom.reactive.widget

import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.reactive.style.ReactiveFlexStyle
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.html.createReactiveHtmlDivElement
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.reactive_list.ReactiveList
import kotlinx.browser.document
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.Node

class Column private constructor(
    private val divElement: HTMLDivElement,
) : Widget() {
    companion object {
        fun of(
            children: ReactiveList<Widget>,
        ): Column = Column(
            divElement = document.createReactiveHtmlDivElement(
                style = ReactiveStyle(
                    displayStyle = Cell.Companion.of(
                        ReactiveFlexStyle(
                            direction = PureFlexDirection.Column,
                        ),
                    ),
                ),
                children = children.rawNodes,
            ),
        )
    }

    override val rawNode: Node
        get() = divElement
}
