package com.almland.pdfebookconverter.domain.upload

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.BufferedImage
import java.io.InputStream
import kotlin.collections.getOrPut
import kotlin.collections.set

internal data class PdfContent(val content: InputStream) {

    private val bufferedContent = content.readAllBytes()
    val text: String by lazy { extractText() }
    val images: Map<Int, MutableMap<Int, BufferedImage>> by lazy { extractImages() }

    /**
     * @return each time a fresh instance of PDDocument from the same InputStream
     */
    private fun loadPdfDocument(): PDDocument = Loader.loadPDF(bufferedContent)

    /**
     * @return the text from pdf document
     */
    private fun extractText(): String = loadPdfDocument().use { PDFTextStripper().getText(it) }

    /**
     * @return map of page index to image index/count on this page to the buffered image
     */
    private fun extractImages(): Map<Int, MutableMap<Int, BufferedImage>> =
        mutableMapOf<Int, MutableMap<Int, BufferedImage>>().apply {
            loadPdfDocument().use {
                it.pages.forEachIndexed { pageIndex, page ->
                    with(page.resources) {
                        xObjectNames.forEachIndexed { nameIndex, name ->
                            val pdxObject = getXObject(name)
                            if (pdxObject is PDImageXObject)
                                getOrPut(pageIndex) { mutableMapOf() }[nameIndex] = pdxObject.image
                        }
                    }
                }
            }
        }
}
