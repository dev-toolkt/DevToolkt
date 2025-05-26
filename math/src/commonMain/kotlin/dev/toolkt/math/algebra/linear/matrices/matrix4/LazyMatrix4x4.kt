package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.math.algebra.linear.vectors.Vector4

internal sealed class LazyMatrix4x4 : Matrix4x4() {
    override val row0: Vector4
        get() = computed.row0

    override val row1: Vector4
        get() = computed.row1

    override val row2: Vector4
        get() = computed.row2

    override val row3: Vector4
        get() = computed.row3

    override val column0: Vector4
        get() = computed.column0

    override val column1: Vector4
        get() = computed.column1

    override val column2: Vector4
        get() = computed.column2

    override val column3: Vector4
        get() = computed.column3

    override fun get(i: Int, j: Int): Double = computed[i, j]

    override val transposed: Matrix4x4
        get() = computed.transposed

    private val computed: Matrix4x4 by lazy {
        compute()
    }

    override fun equalsWithTolerance(
        other: NumericObject,
        tolerance: NumericObject.Tolerance,
    ): Boolean = computed.equalsWithTolerance(
        other,
        tolerance = tolerance,
    )

    protected abstract fun compute(): Matrix4x4
}
