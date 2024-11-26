package com.almland.pdfebookconverter.infrastructure.adaptor.ui.view

import com.almland.pdfebookconverter.application.aggregate.coroutines.CustomScope
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ui.MainLayout
import com.almland.pdfebookconverter.infrastructure.adaptor.ui.dto.Download
import com.vaadin.flow.component.Component
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.UI
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.combobox.ComboBoxVariant
import com.vaadin.flow.component.html.Anchor
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.VaadinIcon.DOWNLOAD
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER
import com.vaadin.flow.component.notification.NotificationVariant
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
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
@Route(value = ContentView.PATH, layout = MainLayout::class)
internal class ContentView(private val aggregateQueryPort: AggregateQueryPort) : Composite<Component>() {

    companion object {
        const val PATH = ""
        private const val ERROR_NOTIFICATION_DURATION = 5000
        private const val ERROR_NOTIFICATION_MESSAGE = "Internal error"
        private val ACCEPTED_FILE_TYPES = arrayOf("application/pdf")
    }

    private lateinit var coroutineScope: CustomScope
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
                            ui.ifPresent { createDownloadLink(memory, it) }
                        }
                        addFileRemovedListener {
                            coroutineScope.onStop()
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

    private fun createDownloadLink(memory: MemoryBuffer, uI: UI) {
        coroutineScope = CustomScope { handlingCoroutineException(uI) }
        coroutineScope.launch(Dispatchers.Default) {
            val target = comboBox.value.target
            val fileName = getFileName(target, memory)
            val download = getDownload(fileName, target, memory, coroutineContext)

            uI.access {
                progressBar.removeFromParent()
                layout.add(createAnchor(download.content, target, memory))
                if (download.suggestions.isNotEmpty()) layout.add(createSuggestion(download.suggestions))
            }
        }
    }

    private fun handlingCoroutineException(uI: UI) {
        uI.access {
            upload.clearFileList()
            progressBar.removeFromParent()
            Notification
                .show(ERROR_NOTIFICATION_MESSAGE, ERROR_NOTIFICATION_DURATION, BOTTOM_CENTER)
                .apply { addThemeVariants(NotificationVariant.LUMO_ERROR) }
        }
    }

    private suspend fun getDownload(
        fileName: String, target: String, memory: MemoryBuffer, context: CoroutineContext
    ): Download = Download(
        awaitAll(getSuggestions(fileName, context), getDownloadContent(fileName, target, memory, context))
    )

    private fun getDownloadContent(
        fileName: String, target: String, memory: MemoryBuffer, context: CoroutineContext
    ): Deferred<InputStream> =
        coroutineScope.async {
            if (isActive.not()) return@async InputStream.nullInputStream()
            else aggregateQueryPort.create(target, fileName, memory.inputStream, context)
        }

    private fun getSuggestions(fileName: String, context: CoroutineContext): Deferred<Collection<String>> =
        coroutineScope.async {
            if (isActive.not()) return@async listOf()
            else aggregateQueryPort.getSuggestions(fileName, context)
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
