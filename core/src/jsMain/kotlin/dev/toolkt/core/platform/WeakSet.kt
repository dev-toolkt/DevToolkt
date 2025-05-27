package dev.toolkt.core.platform

external class WeakSet<T : Any> {
    fun add(value: T): WeakSet<T>

    fun delete(value: T): Boolean

    fun has(value: T): Boolean
}
