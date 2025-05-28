package dev.toolkt.reactive.reactive_list

abstract class DependentReactiveList<E>(
    initialContent: List<E>,
) : ActiveReactiveList<E>() {
    internal val cachedContent = initialContent.toMutableList()

    final override val currentElements: List<E>
        get() = cachedContent.toList()

    protected fun init() {
        changes.listenWeak(
            target = this,
        ) { self, change ->
            change.applyTo(
                mutableList = cachedContent,
            )
        }
    }
}
