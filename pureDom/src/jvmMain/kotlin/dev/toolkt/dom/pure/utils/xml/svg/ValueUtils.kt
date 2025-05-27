package dev.toolkt.dom.pure.utils.xml.svg

import dev.toolkt.dom.pure.PureColor
import org.apache.batik.css.engine.value.Value
import org.w3c.dom.DOMException
import org.w3c.dom.css.CSSPrimitiveValue
import org.w3c.dom.css.CSSValue
import kotlin.math.roundToInt

val Value.primitiveTypeOrNull: Short?
    get() = when (cssValueType) {
        CSSValue.CSS_PRIMITIVE_VALUE -> primitiveType
        else -> null
    }

fun Value.toPureColor(): PureColor? {
    if (primitiveTypeOrNull != CSSPrimitiveValue.CSS_RGBCOLOR) {
        return null
    }

    return PureColor(
        red = red.floatValue.roundToInt(),
        green = green.floatValue.roundToInt(),
        blue = blue.floatValue.roundToInt(),
    )
}

fun Value.toList(): List<Value>? {
    if (cssValueType != CSSValue.CSS_VALUE_LIST) {
        return null
    }

    return object : AbstractList<Value>() {
        override val size: Int
            get() = length

        override fun get(
            index: Int,
        ): Value {
            try {
                return item(index)
            } catch (e: DOMException) {
                throw IndexOutOfBoundsException()
            }
        }
    }
}
