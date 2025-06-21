package dev.toolkt.dom.pure.svg

import dev.toolkt.dom.pure.PureColor

fun PureColor.toHexString(): String = String.format(
    "#%02X%02X%02X",
    red,
    green,
    blue
)
