package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.style.PureDisplayOutside
import dev.toolkt.reactive.cell.Cell
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val displayOutside: Cell<PureDisplayOutside>? = null,
    val displayInsideStyleDeclaration: Cell<ReactiveDisplayInsideStyle>? = null,
) {
    companion object {
        val Default = ReactiveStyle()
    }

    fun bind(styleDeclaration: CSSStyleDeclaration) {
        styleDeclaration.display
    }
}
