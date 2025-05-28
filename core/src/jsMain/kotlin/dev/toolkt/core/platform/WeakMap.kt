package dev.toolkt.core.platform

external class WeakMap<K : Any, V : Any> {
    fun set(key: K, value: V): WeakMap<K, V>

    fun get(key: K): V?

    fun has(key: K): Boolean

    fun delete(key: K): Boolean
}
