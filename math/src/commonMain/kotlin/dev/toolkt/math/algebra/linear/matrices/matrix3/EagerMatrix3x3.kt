package dev.toolkt.math.algebra.linear.matrices.matrix3

import dev.toolkt.math.algebra.linear.vectors.Vector3

internal sealed class EagerMatrix3x3 : Matrix3x3() {
    final override fun apply(argumentVector: Vector3): Vector3 = Vector3(
        a0 = row0.dot(argumentVector),
        a1 = row1.dot(argumentVector),
        a2 = row2.dot(argumentVector),
    )

    final override operator fun times(other: Matrix3x3): Matrix3x3 = columnMajor(
        column0 = apply(other.column0),
        column1 = apply(other.column1),
        column2 = apply(other.column2),
    )
}
