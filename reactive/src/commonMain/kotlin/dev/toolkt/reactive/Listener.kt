package dev.toolkt.reactive

interface Listener<in E> {
    fun handle(event: E)
}

typealias RawListener<E> = (E) -> Unit
