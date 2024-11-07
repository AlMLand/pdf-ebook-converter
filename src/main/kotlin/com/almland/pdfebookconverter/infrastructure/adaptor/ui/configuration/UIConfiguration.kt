package com.almland.pdfebookconverter.infrastructure.adaptor.ui.configuration

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.theme.Theme
import com.vaadin.flow.theme.lumo.Lumo

@Push
@Theme(variant = Lumo.DARK)
internal class UIConfiguration : AppShellConfigurator
