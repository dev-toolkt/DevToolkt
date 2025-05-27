package dev.toolkt.dom.pure.utils.xml.svg

import org.apache.batik.anim.dom.SVGOMDocument
import org.apache.batik.css.engine.CSSContext
import org.apache.batik.css.engine.CSSEngine
import org.apache.batik.css.engine.SVGCSSEngine
import org.apache.batik.css.engine.value.Value
import org.apache.batik.util.ParsedURL
import org.w3c.dom.Element

class MinimalCssContext : CSSContext {
    override fun getSystemColor(p0: String?): Value {
        throw NotImplementedError()
    }

    override fun getDefaultFontFamily(): Value {
        throw NotImplementedError()
    }

    override fun getLighterFontWeight(p0: Float): Float {
        throw NotImplementedError()
    }

    override fun getBolderFontWeight(p0: Float): Float {
        throw NotImplementedError()
    }

    override fun getPixelUnitToMillimeter(): Float {
        throw NotImplementedError()
    }

    override fun getPixelToMillimeter(): Float {
        throw NotImplementedError()
    }

    override fun getMediumFontSize(): Float {
        throw NotImplementedError()
    }

    override fun getBlockWidth(p0: Element?): Float {
        throw NotImplementedError()
    }

    override fun getBlockHeight(p0: Element?): Float {
        throw NotImplementedError()
    }

    override fun checkLoadExternalResource(p0: ParsedURL?, p1: ParsedURL?) {
        throw NotImplementedError()
    }

    override fun isDynamic(): Boolean = false

    override fun isInteractive(): Boolean {
        throw NotImplementedError()
    }

    override fun getCSSEngineForElement(p0: Element): CSSEngine {
        val document = p0.ownerDocument as SVGOMDocument
        val cssEngine = document.cssEngine as SVGCSSEngine
        return cssEngine
    }
}
