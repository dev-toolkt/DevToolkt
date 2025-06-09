package dev.toolkt.core.errors

fun assert(
    condition: Boolean,
    message: () -> String,
) {
    if (!condition) {
        throw AssertionError(message())
    }
}
