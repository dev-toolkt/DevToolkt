package dev.toolkt.reactive.cell_ng

import dev.toolkt.reactive.event_stream_ng.EventStreamNg
import dev.toolkt.reactive.event_stream_ng.SwitchEventStreamNg

class SwitchCellNg<V>(
    nestedCell: CellNg<CellNg<V>>,
) : DependentCellNg<V>(
    initialValue = nestedCell.currentValue.currentValue,
) {
    override val newValues: EventStreamNg<V> = SwitchEventStreamNg(
        nestedCell = nestedCell,
    )
}
