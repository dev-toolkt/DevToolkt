package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.event_stream_ng.EventStreamNg

class HoldCellNg<V>(
    override val newValues: EventStreamNg<V>,
    initialValue: V,
) : DependentCellNg<V>(
    initialValue = initialValue,
)
