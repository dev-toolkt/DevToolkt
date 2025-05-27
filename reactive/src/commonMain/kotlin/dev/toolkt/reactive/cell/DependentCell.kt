package dev.toolkt.reactive.cell

import dev.toolkt.reactive.vertices.cell.DependentCellVertex

internal class DependentCell<V>(
    override val vertex: DependentCellVertex<V>,
) : ActiveCell<V>()
