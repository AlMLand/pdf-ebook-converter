package com.almland.pdfebookconverter.application.aggregate.expractor

import com.almland.pdfebookconverter.domain.PdfDocument
import com.almland.pdfebookconverter.domain.pdffilestructure.Image
import com.almland.pdfebookconverter.domain.pdffilestructure.Page
import com.almland.pdfebookconverter.domain.pdfmetainfo.Author
import com.almland.pdfebookconverter.domain.pdfmetainfo.Description
import java.io.InputStream
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

internal object PdfDocumentExtractor {

    private const val DEFAULT = ""
    private const val DELIMITER = " "

    /**
     * Create a PDF document from input stream.
     * @param content uploaded content
     * @param context current coroutine context
     * @return domain object PdfDocument
     */
    suspend fun create(content: InputStream, context: CoroutineContext): PdfDocument =
        withContext(context + Dispatchers.Default) {
            val bufferedContent = content.readAllBytes()
            PdfDocument(
                extractPages(bufferedContent, coroutineContext),
                extractDescription(bufferedContent, coroutineContext)
            )
        }

    /**
     * Create the new pdfbox stripper, activate stripper, fill the pages with images.
     * @param content PDF as a byte array
     * @param context current coroutine context
     * @return the collection with domain object Page
     */
    private suspend fun extractPages(content: ByteArray, context: CoroutineContext): Collection<Page> =
        withContext(context) {
            loadPdfDocument(content).use { doc ->
                PDFCustomStripper(context)
                    .apply { sortByPosition = true; addMoreFormatting = true; paragraphStart = "\n" }
                    .also { it.getText(doc) }
                    .pages
                    .also { it.forEachIndexed { index, page -> page.images = extractImages(doc, index, context) } }
            }
        }

    /**
     * Extract images from PDF page.
     * @param pdDocument loaded PDF document
     * @param pageIndex current page index
     * @param context current coroutine context
     * @return map (key:image index on current page) to the (value:buffered image)
     */
    private suspend fun extractImages(
        pdDocument: PDDocument, pageIndex: Int, context: CoroutineContext
    ): Collection<Image> = withContext(context) {
        mutableListOf<Image>().apply {
            with(pdDocument.pages[pageIndex].resources) {
                xObjectNames.forEachIndexed { nameIndex, name ->
                    val pdxObject = getXObject(name)
                    if (pdxObject is PDImageXObject) add(Image(nameIndex, pdxObject.image))
                }
            }
        }
    }

    /**
     * Extract PDF description.
     * @param content PDF as a byte array
     * @param context current coroutine context
     * @return domain object Description, which contains book title and author
     * in case when title or author(firstName/lastName) properties are nullable, the default is an empty string
     */
    private suspend fun extractDescription(content: ByteArray, context: CoroutineContext): Description =
        withContext(context) {
            loadPdfDocument(content).use {
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
        }

    /**
     * @param content PDF as a byte array
     * @return each time a fresh instance of PDDocument from the same InputStream
     */
    private fun loadPdfDocument(content: ByteArray): PDDocument = Loader.loadPDF(content)
}