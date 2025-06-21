package dev.toolkt.core

interface ReprObject {
    fun toReprString(): String
}

fun List<ReprObject>.toReprString(): String = when {
    isEmpty() -> "emptyList()"
    else -> {
        val innerString = joinToString("\n") { "${it.toReprString().indent(2)}," }
        "listOf(\n$innerString\n)"
    }
}

fun String.indent(n: Int = 2): String {
    val indent = " ".repeat(n)

    return lines().joinToString("\n") { line -> "$indent$line" }
}

fun String.indentLater(n: Int = 2): String {
    val indent = " ".repeat(n)

    return lines().mapIndexed { index, line ->
        when (index) {
            0 -> line
            else -> "$indent$line"
        }
    }.joinToString("\n")
}
