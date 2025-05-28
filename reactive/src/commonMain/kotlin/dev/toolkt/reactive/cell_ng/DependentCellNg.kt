package dev.toolkt.reactive.cell_ng

abstract class DependentCellNg<V>(
    initialValue: V,
) : ActiveCellNg<V>() {
    internal var cachedValue: V = initialValue

    private fun init() {
        newValues.listenWeak(
            target = this,
        ) { self, newValue ->
            self.cachedValue = newValue
        }
    }

    override val currentValue: V
        get() = cachedValue

    // TODO: Move to subclasses?
    init {
        init()
    }
}
