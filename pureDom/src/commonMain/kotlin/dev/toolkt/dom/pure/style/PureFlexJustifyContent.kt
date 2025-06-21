package dev.toolkt.dom.pure.style

enum class PureFlexJustifyContent(val cssValue: String) {
    Start("flex-start"), End("flex-end"), Center("center"), SpaceBetween("space-between"), SpaceAround("space-around");

    companion object {
        fun parse(
            type: String,
        ): PureFlexJustifyContent = when (type.lowercase()) {
            Start.cssValue -> Start
            End.cssValue -> End
            Center.cssValue -> Center
            SpaceBetween.cssValue -> SpaceBetween
            SpaceAround.cssValue -> SpaceAround
            else -> throw IllegalArgumentException("Unsupported flex-justify-content type: $type")
        }
    }
}
