package dev.toolkt.dom.pure.svg

import dev.toolkt.geometry.transformations.Transformation
import dev.toolkt.core.numeric.NumericObject
import dev.toolkt.core.numeric.equalsWithTolerance
import dev.toolkt.core.numeric.equalsWithToleranceOrNull
import dev.toolkt.dom.pure.PureColor
import org.w3c.dom.Element

abstract class PureSvgShape : PureSvgGraphicsElement() {
    data class Stroke(
        val color: PureColor,
        val width: Double,
        val dashArray: List<Double>? = null,
    ) : NumericObject {
        companion object {
            val default = Stroke(
                color = PureColor.Companion.black,
                width = 1.0,
            )
        }

        fun toDashArrayString(): String? = dashArray?.joinToString(" ") { it.toString() }

        override fun equalsWithTolerance(
            other: NumericObject, tolerance: NumericObject.Tolerance
        ): Boolean = when {
            other !is Stroke -> false
            color != other.color -> false
            !width.equalsWithTolerance(other.width, tolerance) -> false
            !dashArray.equalsWithToleranceOrNull(other.dashArray, tolerance) -> false
            else -> true
        }
    }

    sealed class Fill : NumericObject {
        data class Specified(
            val color: PureColor,
        ) : Fill() {
            companion object {
                val default = Specified(
                    color = PureColor.Companion.black,
                )
            }

            override fun equalsWithTolerance(
                other: NumericObject, tolerance: NumericObject.Tolerance
            ): Boolean = when {
                other !is Specified -> false
                color != other.color -> false
                else -> true
            }

            override fun toFillString(): String = color.toHexString()
        }

        data object None : Fill() {
            override fun toFillString(): String = "none"

            override fun equalsWithTolerance(
                other: NumericObject,
                tolerance: NumericObject.Tolerance
            ): Boolean {
                TODO("Not yet implemented")
            }
        }


        abstract fun toFillString(): String
    }


    final override fun flatten(
        baseTransformation: Transformation,
    ): List<PureSvgShape> = listOf(
        transformVia(transformation = baseTransformation),
    )

    protected fun setupRawShape(
        element: Element,
    ): Element {
        val fill = this.fill
        val stroke = this.stroke
        val markerEndId = this.markerEndId

        return element.apply {
            if (fill != null) {
                setAttribute("fill", fill.toFillString())
            }

            if (stroke != null) {
                setAttribute("stroke", stroke.color.toHexString())
                setAttribute("stroke-width", stroke.width.toString())

                stroke.toDashArrayString()?.let {
                    setAttribute("stroke-dasharray", it)
                }
            }

            if (markerEndId != null) {
                setAttribute("marker-end", "url(#$markerEndId)")
            }
        }
    }

    open val markerEndId: String?
        get() = null

    abstract val stroke: Stroke?

    abstract val fill: Fill?

    abstract fun transformVia(
        transformation: Transformation,
    ): PureSvgShape
}
