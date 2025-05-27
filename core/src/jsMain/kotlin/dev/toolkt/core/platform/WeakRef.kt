package dev.toolkt.core.platform

external class WeakRef<T>(value: T) {
    fun deref(): T?
}
