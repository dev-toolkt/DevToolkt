package dev.toolkt.reactive

interface Listener<in E> {
    fun handle(event: E)
}
