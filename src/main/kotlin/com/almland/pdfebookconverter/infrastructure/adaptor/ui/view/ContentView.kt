package com.almland.pdfebookconverter.infrastructure.adaptor.ui.view

import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ui.MainLayout
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD
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

    private lateinit var upload: Upload
    private lateinit var anchor: Anchor
    private lateinit var target: ComboBox<FileTarget>

    override fun initContent(): Component =
        VerticalLayout().apply {
            setSizeFull()
            style.setFlexWrap(Style.FlexWrap.WRAP)
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

            target = ComboBox<FileTarget>().apply {
                placeholder = "convert pdf to"
                setItems(FileTarget.entries)
                addValueChangeListener { upload.isVisible = true }
            }
            upload = MemoryBuffer().let { memory ->
                Upload(memory).apply {
                    maxFiles = 1
                    isVisible = false
                    setAcceptedFileTypes(*ACCEPTED_FILE_TYPES)
                    addFileRemovedListener { anchor.removeFromParent() }
                    addSucceededListener { add(createAnchor(target.value.target, memory)) }
                }
            }

            add(target, upload)
        }

    private fun createAnchor(target: String, memory: MemoryBuffer): Anchor =
        Anchor(createStreamResource(target, memory), null).apply {
            anchor = this
            add(Button("Download converted", DOWNLOAD.create()))
        }

    private fun createStreamResource(target: String, memory: MemoryBuffer): StreamResource =
        StreamResource(
            getFileName(target, memory),
            InputStreamFactory { aggregateQueryPort.create(target, memory.inputStream) }
        )

    private fun getFileName(target: String, memory: MemoryBuffer): String =
        "${memory.fileName.substringBeforeLast(".")}.$target"
}
