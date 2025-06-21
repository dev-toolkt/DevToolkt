package dev.toolkt.dom.pure.style

enum class PureFlexAlignItems(val cssString: String) {
    Start("flex-start"), End("flex-end"), Center("center"), Baseline("baseline"), Stretch("stretch");

    companion object {
        fun parse(
            type: String,
        ): PureFlexAlignItems = when (type.lowercase()) {
            Start.cssString -> Start
            End.cssString -> End
            Center.cssString -> Center
            Baseline.cssString -> Baseline
            Stretch.cssString -> Stretch
            else -> throw IllegalArgumentException("Unsupported flex-align-items type: $type")
        }
    }
}
