package dev.toolkt.dom.reactive.utils

import org.w3c.dom.css.CSSStyleDeclaration

var CSSStyleDeclaration.gap: String
    get() = this.getPropertyValue("gap")
    set(value) {
        this.setProperty("gap", value)
    }
