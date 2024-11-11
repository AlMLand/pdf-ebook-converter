package com.almland.pdfebookconverter.infrastructure.adaptor.ui.view

import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ui.MainLayout
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.ComboBoxVariant
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.InputStream
import kotlin.concurrent.thread

@Route(value = ContentView.PATH, layout = MainLayout::class)
internal class ContentView(private val aggregateQueryPort: AggregateQueryPort) : Composite<Component>() {

    companion object {
        const val PATH = ""
        private val ACCEPTED_FILE_TYPES = arrayOf("application/pdf")
    }

    private lateinit var upload: Upload
    private lateinit var anchor: Anchor
    private lateinit var layout: VerticalLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var comboBox: ComboBox<FileTarget>

    override fun initContent(): Component =
        VerticalLayout().apply {
            setSizeFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

            val form = VerticalLayout().apply {
                layout = this
                width = "30%"
                minWidth = "20em"

                ComboBox<FileTarget>().apply {
                    comboBox = this
                    setWidthFull()
                    placeholder = "convert T to"
                    setItems(FileTarget.entries)
                    addValueChangeListener { upload.isVisible = true }
                    addThemeVariants(ComboBoxVariant.LUMO_ALIGN_CENTER)
                }
                MemoryBuffer().let { memory ->
                    Upload(memory).apply {
                        upload = this
                        maxFiles = 1
                        setWidthFull()
                        isVisible = false
                        setAcceptedFileTypes(*ACCEPTED_FILE_TYPES)
                        addFileRemovedListener { anchor.removeFromParent() }
                        addSucceededListener { createDownloadProgressBar(); createDownloadLink(memory) }
                    }
                }

                add(comboBox, upload)
            }

            add(form)
        }

    private fun createDownloadProgressBar() {
        ProgressBar().apply {
            layout.add(this)
            setWidthFull()
            progressBar = this
            isIndeterminate = true
        }
    }

    private fun createDownloadLink(memory: MemoryBuffer) {
        ui.ifPresent {
            thread {
                val inputStream = aggregateQueryPort.create(comboBox.value.target, memory.inputStream)
                it.access {
                    layout.add(createAnchor(inputStream, comboBox.value.target, memory))
                    progressBar.removeFromParent()
                }
            }
        }
    }

    private fun createAnchor(inputStream: InputStream, target: String, memory: MemoryBuffer): Anchor =
        Anchor(createStreamResource(inputStream, target, memory), null).apply {
            anchor = this
            setWidthFull()
            add(Button("Download converted", DOWNLOAD.create()).apply { setWidthFull() })
        }

    private fun createStreamResource(inputStream: InputStream, target: String, memory: MemoryBuffer): StreamResource =
        StreamResource(getFileName(target, memory), InputStreamFactory { inputStream })

    private fun getFileName(target: String, memory: MemoryBuffer): String =
        "${memory.fileName.substringBeforeLast(".")}.$target"
}
