package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.style.PureDisplayInside
import dev.toolkt.dom.pure.style.PureDisplayOutside
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveFlowStyle(
    override val outsideType: PureDisplayOutside? = null,
    val someFlexStuff: String,
) : ReactiveDisplayStyle() {
    override val insideType: PureDisplayInside = PureDisplayInside.Flow

    override fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        TODO("Not yet implemented")
    }
}
