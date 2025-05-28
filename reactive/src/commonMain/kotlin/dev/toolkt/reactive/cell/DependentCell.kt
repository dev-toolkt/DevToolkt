package dev.toolkt.reactive.cell

abstract class DependentCell<V>(
    initialValue: V,
) : ActiveCell<V>() {
    internal var cachedValue: V = initialValue

    override val currentValue: V
        get() = cachedValue

    protected fun init() {
        newValues.listenWeak(
            target = this,
        ) { self, newValue ->
            self.cachedValue = newValue
        }
    }
}
