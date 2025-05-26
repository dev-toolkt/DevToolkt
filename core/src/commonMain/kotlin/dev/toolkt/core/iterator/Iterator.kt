package dev.toolkt.core.iterator

fun <T : Any> Iterator<T>.nextOrNull(): T? = when {
    hasNext() -> next()
    else -> null
}
