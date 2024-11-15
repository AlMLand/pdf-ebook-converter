package com.almland.pdfebookconverter.infrastructure.adaptor.ui.view

import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ui.MainLayout
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.ComboBoxVariant
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.Scroller
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.progressbar.ProgressBar
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.dom.Style
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.InputStreamFactory
import com.vaadin.flow.server.StreamResource
import java.io.InputStream
import kotlin.concurrent.thread

@Route(value = ContentView.PATH, layout = MainLayout::class)
internal class ContentView(private val aggregateQueryPort: AggregateQueryPort) : Composite<Component>() {

    companion object {
        const val PATH = ""
        private const val CONTENT_CREATION_THREAD = "createContent"
        private val ACCEPTED_FILE_TYPES = arrayOf("application/pdf")
    }

    private lateinit var upload: Upload
    private lateinit var anchor: Anchor
    private lateinit var scroller: Scroller
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
                        addSucceededListener {
                            createDownloadProgressBar()
                            createDownloadLink(memory)
                        }
                        addFileRemovedListener {
                            if (isAnchorInitialized()) anchor.removeFromParent()
                            if (isScrollerInitialized()) scroller.removeFromParent()
                            if (isProgressBarInitialized()) progressBar.removeFromParent()
                        }
                    }
                }

                add(comboBox, upload)
            }

            add(form)
        }

    private fun isAnchorInitialized(): Boolean = this::anchor.isInitialized

    private fun isScrollerInitialized(): Boolean = this::scroller.isInitialized

    private fun isProgressBarInitialized(): Boolean = this::progressBar.isInitialized

    private fun createDownloadProgressBar() {
        ProgressBar().apply {
            setWidthFull()
            progressBar = this
            isIndeterminate = true
            layout.add(this)
        }
    }

    private fun createDownloadLink(memory: MemoryBuffer) {
        ui.ifPresent {
            thread(name = CONTENT_CREATION_THREAD) {
                val target = comboBox.value.target
                val fileName = getFileName(target, memory)
                val suggestions = aggregateQueryPort.getSuggestions(fileName)
                val inputStream = aggregateQueryPort.create(fileName, target, memory.inputStream)
                it.access {
                    progressBar.removeFromParent()
                    layout.add(createAnchor(inputStream, target, memory))
                    if (suggestions.isNotEmpty()) layout.add(createSuggestion(suggestions))
                }
            }
        }
    }

    private fun createSuggestion(suggestions: Collection<String>): Component =
        Scroller(
            Div(
                fillWithContent(suggestions)
            ).apply { maxHeight = "15em" }
        ).apply { scrollDirection = Scroller.ScrollDirection.VERTICAL; scroller = this }

    private fun fillWithContent(suggestions: Collection<String>): Component =
        VerticalLayout(*suggestions.map { Span(it).apply { setWidthFull() } }.toTypedArray()).apply {
            isPadding = false
            style.setFlexWrap(Style.FlexWrap.WRAP)
        }

    private fun createAnchor(inputStream: InputStream, target: String, memory: MemoryBuffer): Anchor =
        Anchor(createStreamResource(inputStream, target, memory), null).apply {
            anchor = this
            setWidthFull()
            add(Button("Download $target", DOWNLOAD.create()).apply { setWidthFull() })
        }

    private fun createStreamResource(inputStream: InputStream, target: String, memory: MemoryBuffer): StreamResource =
        StreamResource(getFileName(target, memory), InputStreamFactory { inputStream })

    private fun getFileName(target: String, memory: MemoryBuffer): String =
        "${memory.fileName.substringBeforeLast(".")}.$target"
}
