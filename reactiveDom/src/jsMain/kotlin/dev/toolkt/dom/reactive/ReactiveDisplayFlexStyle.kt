package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.style.PureDisplayInside

data class ReactiveDisplayFlexStyle(
    val someFlexStuff: String,
) : ReactiveDisplayInsideStyle() {
    override val type: PureDisplayInside = PureDisplayInside.Flex
}
