package dev.toolkt.dom.reactive.utils.svg

import dev.toolkt.dom.pure.svg.PureSvg
import dev.toolkt.dom.reactive.style.ReactiveStyle
import dev.toolkt.dom.reactive.utils.createReactiveElement
import dev.toolkt.reactive.reactive_list.ReactiveList
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGCircleElement
import org.w3c.dom.svg.SVGElement
import org.w3c.dom.svg.SVGSVGElement

fun Document.createReactiveSvgElement(
    localSvgName: String,
    style: ReactiveStyle? = null,
    children: ReactiveList<Node>? = null,
): SVGElement = createReactiveElement(
    namespace = PureSvg.Namespace,
    name = localSvgName,
    style = style,
    children = children,
) as SVGElement


fun Document.createReactiveSvgSvgCircleElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<SVGElement>? = null,
): SVGSVGElement = createReactiveSvgElement(
    localSvgName = "svg",
    style = style,
    children = children,
) as SVGSVGElement

fun Document.createReactiveSvgCircleElement(
    style: ReactiveStyle? = null,
    children: ReactiveList<SVGElement>? = null,
): SVGCircleElement = createReactiveSvgElement(
    localSvgName = "circle",
    style = style,
    children = children,
) as SVGCircleElement
