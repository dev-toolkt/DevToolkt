package dev.toolkt.reactive.reactive_list_ng

abstract class DependentReactiveListNg<E>(
    initialContent: List<E>,
) : ActiveReactiveListNg<E>() {
    internal val cachedContent = initialContent.toMutableList()

    final override val currentElements: List<E>
        get() = cachedContent.toList()

    private fun init() {
        changes.listenWeak(
            target = this,
        ) { self, change ->
            change.applyTo(
                mutableList = cachedContent,
            )
        }
    }

    // TODO: Move to subclasses?
    init {
        init()
    }
}
