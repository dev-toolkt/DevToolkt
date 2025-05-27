package dev.toolkt.dom.pure.style

enum class PureDisplayInside(val type: String) {
    Flow("flow"), Flex("flex");

    companion object {
        fun parse(
            type: String,
        ): PureDisplayInside = when (type.lowercase()) {
            "flow" -> Flow
            "flex" -> Flex
            else -> throw UnsupportedOperationException("Unsupported display-inside type: $type")
        }
    }
}
