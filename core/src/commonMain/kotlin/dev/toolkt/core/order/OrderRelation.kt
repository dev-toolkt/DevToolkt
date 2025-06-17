package dev.toolkt.core.order

sealed interface OrderRelation {
    sealed interface Inequal

    data object Smaller : Inequal

    data object Greater : Inequal

    data object Equal : OrderRelation
}
