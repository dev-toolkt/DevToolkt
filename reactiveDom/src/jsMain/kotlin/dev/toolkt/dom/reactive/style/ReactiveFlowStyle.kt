package dev.toolkt.dom.reactive.style

import dev.toolkt.dom.pure.style.PureDisplayInside
import dev.toolkt.dom.pure.style.PureDisplayOutside
import dev.toolkt.reactive.Subscription
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveFlowStyle(
    override val outsideType: PureDisplayOutside? = null,
    val someFlexStuff: String,
) : ReactiveDisplayStyle() {
    override val insideType: PureDisplayInside = PureDisplayInside.Flow

    override fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ): Subscription.Noop = Subscription.Noop
}
