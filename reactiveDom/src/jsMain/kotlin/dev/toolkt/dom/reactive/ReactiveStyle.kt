package dev.toolkt.dom.reactive

import dev.toolkt.reactive.Subscription
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.bindNested
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveStyle(
    val displayStyle: Cell<ReactiveDisplayStyle>? = null,
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
    }
}
