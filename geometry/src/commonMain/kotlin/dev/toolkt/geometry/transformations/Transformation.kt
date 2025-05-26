package dev.toolkt.geometry.transformations

import dev.toolkt.geometry.Point
import dev.toolkt.geometry.transformations.PrimitiveTransformation.Universal
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance

sealed class Transformation : NumericObject {
    object Identity : Transformation() {
        override fun combineWith(laterTransformations: List<StandaloneTransformation>): CombinedTransformation {
            return CombinedTransformation(
                standaloneTransformations = laterTransformations,
            )
        }

        override fun toSvgTransformationString(): String = ""

        override val toUniversal: Universal
            get() = Universal()

        override val standaloneTransformations: List<PrimitiveTransformation> = emptyList()

        override val primitiveTransformations: List<PrimitiveTransformation> = emptyList()

        override fun transform(point: Point): Point = point

        override fun equalsWithTolerance(
            other: NumericObject,
            tolerance: NumericObject.Tolerance,
        ): Boolean {
            TODO("Not yet implemented")
        }
    }

    companion object {
        fun combine(
            transformations: List<Transformation>,
        ): CombinedTransformation = CombinedTransformation(
            standaloneTransformations = transformations.flatMap {
                it.standaloneTransformations
            },
        )
    }

    fun combineWith(
        laterTransformation: Transformation,
    ): CombinedTransformation = combineWith(
        laterTransformations = laterTransformation.standaloneTransformations,
    )

    abstract fun combineWith(
        laterTransformations: List<StandaloneTransformation>,
    ): CombinedTransformation

    abstract fun toSvgTransformationString(): String

    abstract val toUniversal: Universal

    val toProjection: Projection?
        get() {
            val universal = toUniversal

            val b = universal.b
            val c = universal.c

            if (b != 0.0 || c != 0.0) {
                return null
            }

            val sx = universal.a
            val sy = universal.d

            return Projection(
                scaling = PrimitiveTransformation.Scaling(
                    sx = sx,
                    sy = sy,
                ),
                translation = PrimitiveTransformation.Translation(
                    tx = universal.tx,
                    ty = universal.ty,
                ),
            )
        }

    val toMovement: Movement?
        get() {
            val universal = toUniversal

            val a = universal.a
            val b = universal.b
            val c = universal.c
            val d = universal.d

            if (!a.equalsWithTolerance(d) || !b.equalsWithTolerance(-c)) {
                return null
            }

            return Movement(
                rotation = PrimitiveTransformation.Rotation.trigonometric(
                    cosFi = a,
                    sinFi = b,
                ),
                translation = PrimitiveTransformation.Translation(
                    tx = universal.tx,
                    ty = universal.ty,
                ),
            )
        }

    /**
     * Simple components of the transformation in the order of application.
     */
    abstract val standaloneTransformations: List<StandaloneTransformation>

    /**
     * Primitive components of the transformation in the order of application.
     */
    abstract val primitiveTransformations: List<PrimitiveTransformation>

    abstract fun transform(
        point: Point,
    ): Point
}

