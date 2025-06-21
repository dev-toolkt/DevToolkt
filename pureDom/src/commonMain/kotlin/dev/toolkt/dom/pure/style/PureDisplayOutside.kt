package dev.toolkt.dom.pure.style

enum class PureDisplayOutside(val cssString: String) {
    Block("block"), Inline("inline");

    companion object {
        fun parse(
            type: String,
        ): PureDisplayOutside = when (type.lowercase()) {
            Block.cssString -> Block
            Inline.cssString -> Inline
            else -> throw IllegalArgumentException("Unknown display-outside type: $type")
        }
    }
}
