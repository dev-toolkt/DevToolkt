package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.style.PureDisplayInside
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveDisplayFlexStyle(
    val someFlexStuff: String,
) : ReactiveDisplayInsideStyle() {
    override val type: PureDisplayInside = PureDisplayInside.Flex

    override fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        TODO("Not yet implemented")
    }


}
