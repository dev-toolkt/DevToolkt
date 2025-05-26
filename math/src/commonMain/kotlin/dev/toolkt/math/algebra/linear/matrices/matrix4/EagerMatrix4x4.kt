package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.vectors.Vector4

internal sealed class EagerMatrix4x4 : Matrix4x4() {
    final override fun apply(
        argumentVector: Vector4,
    ): Vector4 = Vector4(
        a0 = row0.dot(argumentVector),
        a1 = row1.dot(argumentVector),
        a2 = row2.dot(argumentVector),
        a3 = row3.dot(argumentVector),
    )

    final override operator fun times(
        other: Matrix4x4,
    ): Matrix4x4 = columnMajor(
        column0 = apply(other.column0),
        column1 = apply(other.column1),
        column2 = apply(other.column2),
        column3 = apply(other.column3),
    )
}
