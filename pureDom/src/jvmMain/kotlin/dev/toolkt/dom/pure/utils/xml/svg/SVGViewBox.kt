package dev.toolkt.dom.pure.utils.xml.svg

data class SVGViewBox(
    val xMin: Double,
    val yMin: Double,
    val width: Double,
    val height: Double,
) {
    companion object {
        fun fromSvgString(
            viewBox: String,
        ): SVGViewBox {
            val parts = viewBox.split(" ")

            require(parts.size == 4) { "Invalid viewBox format" }

            val (xMin, yMin, width, height) = parts

            return SVGViewBox(
                xMin.toDouble(),
                yMin.toDouble(),
                width.toDouble(),
                height.toDouble(),
            )
        }
    }

    fun toSvgString(): String = "$xMin $yMin $width $height"
}
