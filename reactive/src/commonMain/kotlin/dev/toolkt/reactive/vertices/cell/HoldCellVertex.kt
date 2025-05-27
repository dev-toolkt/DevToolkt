package dev.toolkt.reactive.vertices.cell

import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.vertices.ManagedVertex

internal class HoldCellVertex<V>(
    private val values: ManagedVertex<V>,
    initialValue: V,
) : DependentCellVertex<V>(
    initialValue = initialValue,
) {
    override val kind: String = "Hold"

    override fun buildHybridSubscription() = values.subscribeHybrid(
        listener = object : Listener<V> {
            override fun handle(value: V) {
                update(value)
            }
        },
    )

    init {
        init()
    }
}
