package com.almland.pdfebookconverter.domain

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.BufferedImage
import kotlin.collections.set

internal object PdfContentBuilderIntern {
    /**
     * @return each time a fresh instance of PDDocument from the same InputStream
     */
    private fun loadPdfDocument(content: ByteArray): PDDocument = Loader.loadPDF(content)

    /**
     * @return the text from pdf document
     */
    fun extractText(content: ByteArray): String = loadPdfDocument(content).use {
        PDFTextStripper()
            .apply { sortByPosition = true; addMoreFormatting = true }
            .getText(it)
    }

    /**
     * @return map of page index to image index/count on this page to the buffered image
     */
    fun extractImages(content: ByteArray): Map<Int, MutableMap<Int, BufferedImage>> =
        mutableMapOf<Int, MutableMap<Int, BufferedImage>>().apply {
            loadPdfDocument(content).use {
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
