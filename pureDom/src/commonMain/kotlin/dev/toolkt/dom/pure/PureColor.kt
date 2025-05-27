package dev.toolkt.dom.pure

data class PureColor(
    val red: Int,
    val green: Int,
    val blue: Int,
) {
    companion object {
        val black = PureColor(0, 0, 0)
        val red = PureColor(255, 0, 0)
        val green = PureColor(0, 255, 0)
        val blue = PureColor(0, 0, 255)

        val lightGray = PureColor(211, 211, 211)
    }

    init {
        require(red in 0..255) { "Red value must be between 0 and 255" }
        require(green in 0..255) { "Green value must be between 0 and 255" }
        require(blue in 0..255) { "Blue value must be between 0 and 255" }
    }
}
