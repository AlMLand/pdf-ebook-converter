package com.almland.pdfebookconverter.infrastructure.adaptor.ui

import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.html.H1

internal class MainLayout : AppLayout() {

    init {
        addToNavbar(
            DrawerToggle(),
            H1("T converter")
        )
    }
}
