package dev.toolkt.reactive.reactive_list_ng

abstract class ActiveReactiveListNg<E>() : ReactiveListNg<E>() {
    final override fun <Er> map(
        transform: (E) -> Er,
    ): ReactiveListNg<Er> = MapReactiveListNg(
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
