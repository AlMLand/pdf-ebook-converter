package com.almland.pdfebookconverter.domain

import java.awt.image.BufferedImage
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.text.PDFTextStripper

internal object PdfDocumentBuilder {

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
        val textStripper = PDFTextStripper().apply {
            sortByPosition = true; addMoreFormatting = true; paragraphStart = "\n"
        }
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
    private fun extractText(pdDocument: PDDocument, textStripper: PDFTextStripper, pageIndex: Int): Collection<String> {
        with(textStripper) { startPage = pageIndex + 1; endPage = pageIndex + 1 }
        return textStripper
            .getText(pdDocument)
            .split(textStripper.paragraphStart)
            .let { groupLines(it) }
    }

    /**
     * Method group the lines by algorithm: if the first letter of a line is an upper letter,
     * then other lines witch starts with a lower letter will be concatenated together and so in loop.
     * @param lines that is a collection with all text lines from one page
     * @return collection grouped lines
     */
    private fun groupLines(lines: Collection<String>): Collection<String> =
        mutableListOf<String>().apply {
            var currentParagraph: String? = null

            lines.forEach { line ->
                if (isNewParagraphStart(line)) {
                    currentParagraph?.let { add(it) }
                    currentParagraph = line
                } else currentParagraph = processLineStartsInLowerCase(currentParagraph, line)
            }

            currentParagraph?.let { add(it) }
        }

    /**
     * Method checks when current line should to be a start for a new paragraph.
     * @param line current line
     * @return true when the current line should to be a new paragraph, false when not
     */
    private fun isNewParagraphStart(line: String): Boolean = isUpperCase(line) || isDigit(line) || isSymbol(line)

    /**
     * Often some paragraphs start with symbol like a '-' etc.
     * This method checks if the first letter is not a letter.
     * It is unnecessary to check if it is a digit that does the method 'isDigit(…)'
     * @param line current line
     * @return true when digit and false when not
     */
    private fun isSymbol(line: String): Boolean = line.firstOrNull()?.isLetter()?.not() == true

    /**
     * Often chapters starts with a digit. This method checks if the first letter is a digit.
     * @param line current line
     * @return true when digit and false when not
     */
    private fun isDigit(line: String): Boolean = line.firstOrNull()?.isDigit() == true

    /**
     * Method shows when currentGroup is null, then returns line immediately without transformations,
     * when currentGroup is not null, then currentGroup will be concatenated with new line
     * @param currentGroup the current line holder
     * @param line current line
     * @return a new value for a currentGroup
     */
    private fun processLineStartsInLowerCase(currentGroup: String?, line: String): String =
        if (currentGroup == null) line
        else "$currentGroup $line"

    /**
     * Method checks: is the first letter in the upper case.
     * @param line one line
     * @return true when in upper case and false when not
     */
    private fun isUpperCase(line: String): Boolean = line.firstOrNull()?.isUpperCase() == true

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
