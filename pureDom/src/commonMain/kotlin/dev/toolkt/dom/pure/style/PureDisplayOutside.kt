package dev.toolkt.dom.pure.style

enum class PureDisplayOutside(val type: String) {
    BLOCK("block"), INLINE("inline");

    companion object {
        fun parse(
            type: String,
        ): PureDisplayOutside = when (type.lowercase()) {
            "block" -> BLOCK
            "inline" -> INLINE
            else -> throw IllegalArgumentException("Unknown display-outside type: $type")
        }
    }
}
