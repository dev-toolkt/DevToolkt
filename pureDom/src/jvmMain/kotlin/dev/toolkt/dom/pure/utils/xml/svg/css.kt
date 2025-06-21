package dev.toolkt.dom.pure.utils.xml.svg

import org.w3c.dom.css.CSSPrimitiveValue
import org.w3c.dom.css.CSSStyleDeclaration
import kotlin.math.roundToInt

fun CSSStyleDeclaration.setProperty(propertyName: String, value: String) {
    setProperty(propertyName, value, "")
}

fun CSSPrimitiveValue.getIntNumberValue(): Int = getFloatValue(CSSPrimitiveValue.CSS_NUMBER).roundToInt()
