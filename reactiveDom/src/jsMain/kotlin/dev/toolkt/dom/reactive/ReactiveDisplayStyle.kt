package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.style.PureDisplayInside
import dev.toolkt.dom.pure.style.PureDisplayOutside
import dev.toolkt.reactive.Subscription
import org.w3c.dom.css.CSSStyleDeclaration

sealed class ReactiveDisplayStyle {
    abstract val outsideType: PureDisplayOutside?

    abstract val insideType: PureDisplayInside

    val displayString: String
        get() = listOfNotNull(
            outsideType?.cssString,
            insideType.type,
        ).joinToString(separator = " ")

    abstract fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ): Subscription
}
