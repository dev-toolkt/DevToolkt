package dev.toolkt.dom.reactive

import dev.toolkt.dom.pure.PureDimension
import dev.toolkt.dom.pure.style.PureDisplayInside
import dev.toolkt.dom.pure.style.PureDisplayOutside
import dev.toolkt.dom.pure.style.PureFlexAlignItems
import dev.toolkt.dom.pure.style.PureFlexDirection
import dev.toolkt.dom.pure.style.PureFlexJustifyContent
import dev.toolkt.dom.reactive.utils.gap
import org.w3c.dom.css.CSSStyleDeclaration

data class ReactiveFlexStyle(
    override val outsideType: PureDisplayOutside? = null,
    val direction: PureFlexDirection? = null,
    val alignItems: PureFlexAlignItems? = null,
    val justifyContent: PureFlexJustifyContent? = null,
    val gap: PureDimension<*>? = null,
) : ReactiveDisplayStyle() {
    override val insideType: PureDisplayInside = PureDisplayInside.Flex

    override fun bind(
        styleDeclaration: CSSStyleDeclaration,
    ) {
        direction?.let {
            styleDeclaration.flexDirection = it.cssValue
        }

        alignItems?.let {
            styleDeclaration.alignItems = it.cssString
        }

        justifyContent?.let {
            styleDeclaration.justifyContent = it.cssValue
        }

        gap?.let {
            styleDeclaration.gap = it.toDimensionString()
        }
    }
}
