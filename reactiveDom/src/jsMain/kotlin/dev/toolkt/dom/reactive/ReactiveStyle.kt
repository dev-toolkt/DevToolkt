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
        when {
            // If the inside display type is provided, we always use the multi-keyword
            // syntax, e.g. `inline flex` or `block flow` (even if the outside
            // display type wasn't given explicitly)
            displayInsideStyleDeclaration != null -> {
                val effectiveDisplayOutside = displayOutside ?: Cell.of(PureDisplayOutside.Block)

                Cell.zip2(
                    effectiveDisplayOutside,
                    displayInsideStyleDeclaration,
                ).bind(
                    target = styleDeclaration,
                    update = { it, (displayOutsideNow, displayInsideStyleNow) ->
                        it.display = "${displayOutsideNow.type} ${displayInsideStyleNow.type}"

                        // Bind the inside display specific properties
                        displayInsideStyleNow.bind(it)
                    },
                )
            }

            // If the inside display type is omitted, we use the single-keyword syntax,
            // e.g `inline` or `block`
            displayOutside != null -> {
                displayOutside.bind(
                    target = styleDeclaration,
                    update = { it, displayOutsideNow ->
                        it.display = displayOutsideNow.type
                    },
                )
            }
        }
    }
}
