package com.almland.pdfebookconverter.domain

import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.BufferedImage

internal object PdfContentBuilder {

    private const val DEFAULT = ""
    private const val DELIMITER = " "

    /**
     * @param content PDF as a byte array
     * @return each time a fresh instance of PDDocument from the same InputStream
     */
    private fun loadPdfDocument(content: ByteArray): PDDocument = Loader.loadPDF(content)

    /**
     * @param content PDF as a byte array
     * @return domain object Description, which contains book title and author
     * in case when title or author(firstName/lastName) properties are nullable, the default is an empty string
     */
    fun extractDescription(content: ByteArray): Description = loadPdfDocument(content).use {
        with(it.documentInformation) {
            Description(
                title ?: DEFAULT,
                Author(
                    author?.substringBefore(DELIMITER) ?: DEFAULT,
                    author?.substringAfter(DELIMITER) ?: DEFAULT
                )
            )
        }
    }

    /**
     * @param content PDF as a byte array
     * @return the collection with domain object Page
     */
    fun extractPages(content: ByteArray): Collection<Page> = loadPdfDocument(content).use {
        val textStripper = PDFTextStripper().apply { sortByPosition = true; addMoreFormatting = true }
        List<Page>(size = it.pages.count) { pageIndex ->
            Page(
                pageIndex,
                extractText(it, textStripper, pageIndex),
                extractImages(it, pageIndex)
            )
        }
    }

    /**
     * @param pdDocument loaded PDF document
     * @param textStripper document text extractor
     * @param pageIndex current page index
     * @return text for current document page
     */
    private fun extractText(pdDocument: PDDocument, textStripper: PDFTextStripper, pageIndex: Int): String {
        with(textStripper) { startPage = pageIndex + 1; endPage = pageIndex + 1 }
        return textStripper.getText(pdDocument)
    }

    /**
     * @param pdDocument loaded PDF document
     * @param pageIndex current page index
     * @return map (key:image index on current page) to the (value:buffered image)
     */
    private fun extractImages(pdDocument: PDDocument, pageIndex: Int): Map<Int, BufferedImage> =
        mutableMapOf<Int, BufferedImage>().apply {
            with(pdDocument.pages[pageIndex].resources) {
                xObjectNames.forEachIndexed { nameIndex, name ->
                    val pdxObject = getXObject(name)
                    if (pdxObject is PDImageXObject) put(nameIndex, pdxObject.image)
                }
            }
        }
}
