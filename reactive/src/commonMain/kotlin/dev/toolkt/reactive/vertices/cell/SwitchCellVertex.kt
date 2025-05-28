package dev.toolkt.reactive.vertices.cell

import dev.toolkt.reactive.HybridSubscription
import dev.toolkt.reactive.Listener
import dev.toolkt.reactive.cell.ActiveCell
import dev.toolkt.reactive.cell.Cell
import dev.toolkt.reactive.cell.ConstCell

class SwitchCellVertex<V>(
    private val nestedCell: CellVertex<Cell<V>>,
) : DependentCellVertex<V>(
    initialValue = nestedCell.currentValue.currentValue,
) {
    override val kind: String = "Switch"

    override fun buildHybridSubscription() = object : HybridSubscription {
        private val outerSubscription = nestedCell.subscribeHybridRaw {
            val newInnerCell = it.newValue

            update(newInnerCell.currentValue)

            resubscribeToInner(newInnerCell = newInnerCell)
        }

        private var innerSubscription = subscribeToInner(
            innerCell = nestedCell.currentValue,
        )

        private fun subscribeToInner(
            innerCell: Cell<V>,
        ): HybridSubscription = when (innerCell) {
            is ActiveCell<V> -> innerCell.vertex.subscribeHybridRaw {
                update(it.newValue)
            }

            is ConstCell<V> -> HybridSubscription.Noop
        }

        private fun resubscribeToInner(
            newInnerCell: Cell<V>,
        ) {
            innerSubscription.cancel()
            innerSubscription = subscribeToInner(innerCell = newInnerCell)
        }

        override fun cancel() {
            outerSubscription.cancel()
            innerSubscription.cancel()
        }

        override fun updateStrength(newStrength: ListenerStrength) {
            outerSubscription.updateStrength(newStrength = newStrength)
            innerSubscription.updateStrength(newStrength = newStrength)
        }
    }

    init {
        init()
    }
}
