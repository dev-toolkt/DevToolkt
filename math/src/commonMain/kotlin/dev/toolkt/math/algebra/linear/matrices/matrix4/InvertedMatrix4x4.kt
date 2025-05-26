package dev.toolkt.math.algebra.linear.matrices.matrix4

import dev.toolkt.math.algebra.linear.vectors.Vector4

internal class InvertedMatrix4x4(
    /**
     * The LU(P) decomposition of the original matrix.
     */
    private val lupDecomposition: LupDecomposition,
) : LazyMatrix4x4() {
    override fun apply(argumentVector: Vector4): Vector4 {
        val yVector = lMatrix.solveByForwardSubstitution(
            yVector = pMatrix.apply(argumentVector),
        )

        val xVector = uMatrix.solveByBackSubstitution(
            yVector = yVector,
        )

        return xVector
    }

    override operator fun times(
        other: Matrix4x4,
    ): Matrix4x4 {
        val yMatrix = lMatrix.solveByForwardSubstitution(
            yMatrix = pMatrix * other,
        )

        val xMatrix = uMatrix.solveByBackSubstitution(
            yMatrix = yMatrix,
        )

        return xMatrix
    }

    override fun compute() = this * identity

    private val lMatrix: Matrix4x4
        get() = lupDecomposition.l

    private val uMatrix: Matrix4x4
        get() = lupDecomposition.u

    private val pMatrix: Matrix4x4
        get() = lupDecomposition.p
}
