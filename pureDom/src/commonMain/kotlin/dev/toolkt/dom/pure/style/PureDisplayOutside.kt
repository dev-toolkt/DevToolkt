package dev.toolkt.dom.pure.style

enum class PureDisplayOutside(val type: String) {
    Block("block"), Inline("inline");

    companion object {
        fun parse(
            type: String,
        ): PureDisplayOutside = when (type.lowercase()) {
            "block" -> Block
            "inline" -> Inline
            else -> throw IllegalArgumentException("Unknown display-outside type: $type")
        }
    }
}
