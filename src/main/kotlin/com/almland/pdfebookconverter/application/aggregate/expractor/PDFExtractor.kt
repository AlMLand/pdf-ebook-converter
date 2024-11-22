package com.almland.pdfebookconverter.application.aggregate.expractor

import com.almland.pdfebookconverter.domain.pdffilestructure.Image
import com.almland.pdfebookconverter.domain.pdffilestructure.Page
import com.almland.pdfebookconverter.domain.pdfmetainfo.Author
import com.almland.pdfebookconverter.domain.pdfmetainfo.Description
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

internal object PDFExtractor {

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
     * Create the new pdfbox stripper, activate stripper, fill the pages with images.
     * @param content PDF as a byte array
     * @return the collection with domain object Page
     */
    fun extractPages(content: ByteArray): Collection<Page> = loadPdfDocument(content).use { doc ->
        PDFCustomStripper()
            .apply { sortByPosition = true; addMoreFormatting = true; paragraphStart = "\n" }
            .also { it.getText(doc) }
            .pages
            .also { it.forEachIndexed { index, page -> page.images = extractImages(doc, index) } }
    }

    /**
     * @param pdDocument loaded PDF document
     * @param pageIndex current page index
     * @return map (key:image index on current page) to the (value:buffered image)
     */
    private fun extractImages(pdDocument: PDDocument, pageIndex: Int): Collection<Image> =
        mutableListOf<Image>().apply {
            with(pdDocument.pages[pageIndex].resources) {
                xObjectNames.forEachIndexed { nameIndex, name ->
                    val pdxObject = getXObject(name)
                    if (pdxObject is PDImageXObject) add(Image(nameIndex, pdxObject.image))
                }
            }
        }
}
