package dev.toolkt.dom.reactive

import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.bindNested
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val displayInsideStyleDeclaration: Cell<ReactiveDisplayStyle>? = null,
) {
    companion object {
        val Default = ReactiveStyle()
    }

    fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        displayInsideStyleDeclaration?.bindNested(
            target = styleDeclaration,
            updateOuter = { it, reactiveDisplayStyle ->
                it.display = reactiveDisplayStyle.displayString
            },
            bindInner = { it, reactiveDisplayStyle ->
                reactiveDisplayStyle.bind(styleDeclaration = it)
            },
        )
    }
}
