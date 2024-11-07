package com.almland.pdfebookconverter.infrastructure.adaptor.ui.view

import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ui.MainLayout
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.H3
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.dom.Style
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource

@Route(value = ContentView.PATH, layout = MainLayout::class)
internal class ContentView(private val aggregateQueryPort: AggregateQueryPort) : Composite<Component>() {

    companion object {
        const val PATH = ""
        private val ACCEPTED_FILE_TYPES = arrayOf("application/pdf")
    }

    override fun initContent(): Component =
        VerticalLayout().apply {
            setSizeFull()
            style.setFlexWrap(Style.FlexWrap.WRAP)
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

            val label = H3("Pdf to fb2")
            val upload = MemoryBuffer().let { memory ->
                Upload(memory).apply {
                    maxFiles = 1
                    setAcceptedFileTypes(*ACCEPTED_FILE_TYPES)
                    addSucceededListener {
                        add(
                            Anchor(
                                StreamResource(
                                    "download.fb2",
                                    InputStreamFactory { aggregateQueryPort.createFB2(memory.inputStream) }
                                ),
                                null
                            ).apply { add(Button("Download converted", VaadinIcon.DOWNLOAD.create())) }
                        )
                    }
                }
            }

            add(label, upload)
        }
}
