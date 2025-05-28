package dev.toolkt.reactive.vertices.cell

import dev.toolkt.reactive.vertices.ManagedVertex

internal class HoldCellVertex<V>(
    private val values: ManagedVertex<V>,
    initialValue: V,
) : DependentCellVertex<V>(
    initialValue = initialValue,
) {
    override val kind: String = "Hold"

    override fun buildHybridSubscription() = values.subscribeHybridRaw {
        update(it)
    }

    init {
        init()
    }
}
