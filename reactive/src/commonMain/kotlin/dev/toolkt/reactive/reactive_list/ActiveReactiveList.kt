package dev.toolkt.reactive.reactive_list

abstract class ActiveReactiveList<E>() : ReactiveList<E>() {
    final override fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveList<Er> = MapReactiveList(
        source = this,
        transform = transform,
    )

    final override fun <T : Any> bind(
        target: T,
        extract: (T) -> MutableList<in E>,
    ) {
        copyNow(mutableList = extract(target))

        changes.pipe(
            target = target,
        ) { target, change ->
            change.applyTo(mutableList = extract(target))
        }
    }
}
