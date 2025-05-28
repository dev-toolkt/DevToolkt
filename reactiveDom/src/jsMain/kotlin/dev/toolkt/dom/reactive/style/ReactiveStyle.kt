package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.PureColor
import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.bindNested
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val displayStyle: Cell<ReactiveDisplayStyle>? = null,
    val width: Cell<PureDimension<*>>? = null,
    val height: Cell<PureDimension<*>>? = null,
    val backgroundColor: Cell<PureColor>? = null,
) {
    companion object {
        val Default = ReactiveStyle()
    }

    fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        displayStyle?.bindNested(
            target = styleDeclaration,
            bindInner = { it, reactiveDisplayStyle ->
                it.display = reactiveDisplayStyle.displayString

                val innerSubscription = reactiveDisplayStyle.bind(styleDeclaration = it)

                object : Subscription {
                    override fun cancel() {
                        innerSubscription.cancel()

                        it.display = ""
                    }
                }
            },
        )

        width?.bind(
            target = styleDeclaration,
        ) { it, dimension ->
            it.width = dimension.toDimensionString()
        }

        height?.bind(
            target = styleDeclaration,
        ) { it, dimension ->
            it.height = dimension.toDimensionString()
        }

        backgroundColor?.bind(
            target = styleDeclaration,
        ) { it, color ->
            it.backgroundColor = color.cssString
        }
    }
}
