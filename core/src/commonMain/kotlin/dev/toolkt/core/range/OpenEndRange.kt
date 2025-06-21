package dev.toolkt.core.range

fun OpenEndRange<Int>.overlaps(other: OpenEndRange<Int>): Boolean {
    return start < other.endExclusive && endExclusive > other.start
}
