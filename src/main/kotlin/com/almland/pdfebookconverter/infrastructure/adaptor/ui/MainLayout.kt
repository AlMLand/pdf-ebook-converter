package com.almland.pdfebookconverter.infrastructure.adaptor.ui

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.dom.Style.Position.ABSOLUTE

internal class MainLayout : AppLayout() {

    init {
        addToNavbar(
            H1("T converter").apply {
                style
                    .setFontSize("var(--lumo-font-size-l)")
                    .setLeft("var(--lumo-space-l)")
                    .setPosition(ABSOLUTE)
            }
        )
    }
}
