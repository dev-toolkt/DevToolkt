package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.style.PureDisplayInside
import org.w3c.dom.css.CSSStyleDeclaration

sealed class ReactiveDisplayInsideStyle {
    abstract val type: PureDisplayInside

    abstract fun bind(
        styleDeclaration: CSSStyleDeclaration,
    )
}
