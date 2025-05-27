package dev.toolkt.dom.pure.style

enum class PureFlexDirection(val cssValue: String) {
    Row("row"), Column("column");

    companion object {
        fun parse(
            type: String,
        ): PureFlexDirection = when (type.lowercase()) {
            Row.cssValue -> Row
            Column.cssValue -> Column
            else -> throw IllegalArgumentException("Unsupported flex-direction type: $type")
        }
    }
}
