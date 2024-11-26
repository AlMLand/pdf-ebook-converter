package com.almland.pdfebookconverter.application.aggregate.expractor

import com.almland.pdfebookconverter.domain.pdffilestructure.Line
import com.almland.pdfebookconverter.domain.pdffilestructure.Page
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.runBlocking
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.pdfbox.text.TextPosition

/**
 * Stripper allows mark lines as bold or not, after processing contains a collection with all Pages.
 */
@Suppress("TooManyFunctions")
internal class PDFCustomStripper(private val context: CoroutineContext) : PDFTextStripper() {

    companion object {
        private const val TEXT_POSITION_FIRST = 0
    }

    private var currentPage: Page? = null
    private val pageIndex: AtomicInteger = AtomicInteger(0)
    internal var pages: MutableCollection<Page> = mutableListOf()
        private set

    /**
     * Additional behavior: add pages, increment pageIndex.
     */
    override fun endPage(page: PDPage?) {
        addPages()
        pageIndex.incrementAndGet()
        super.endPage(page)
    }

    /**
     * When true, add created pages to the collection and set currentPage to null,
     * when false. That means that the page doesn't have any text content,
     * then creates a new Page, which contains only current pageIndex, empty lines, empty images.
     */
    private fun addPages() {
        if (currentPage != null) {
            pages.add(currentPage!!)
            currentPage = null
        } else pages.add(Page(pageIndex.get(), mutableListOf(), emptyList()))
    }

    /**
     * Additional behavior: set pageIndex to 0, set currentPage to null, group extracted lines.
     */
    override fun endDocument(document: PDDocument?) = runBlocking(context) {
        pageIndex.set(0)
        currentPage = null
        groupLines(pages)
        super.endDocument(document)
    }

    /**
     * Additional behavior: when 'text' and 'textPositions' are not null and not empty ->
     * that means if currentPage is null, it will be newly created with actual pageIndex,
     * actual text and bold parameters (Line instance), images are not in use case -> empty collection.
     * When currentPage is not null, then the Line with text and bold will be added to the instance of currentPage
     */
    override fun writeString(text: String?, textPositions: List<TextPosition?>?) {
        if (isTextNotNullNotEmpty(text) && textPositions != null && textPositions.isNotEmpty())
            Line(text, getFontName(textPositions)).let { line ->
                if (currentPage == null) {
                    currentPage = Page(
                        pageIndex.get(),
                        mutableListOf(line),
                        emptyList()
                    )
                } else currentPage?.lines?.add(line)
            }

        super.writeString(text, textPositions)
    }

    private fun getFontName(textPositions: List<TextPosition?>): String = textPositions[TEXT_POSITION_FIRST]!!.font.name

    /**
     * Check when the ongoing text is not null and not empty.
     * @param text current string
     * @return boolean result
     */
    private fun isTextNotNullNotEmpty(text: String?): Boolean = text?.isNotBlank() == true

    /**
     * Method group the lines by algorithm: if the first letter of a line is an upper letter,
     * then other lines witch starts with a lower letter will be concatenated together and so in loop.
     * In process, each 'page.lines' collection will be reassigned.
     * @param pages that is a collection that contains all PDF document pages
     */
    private fun groupLines(pages: MutableCollection<Page>) {
        pages.forEach { page ->
            page.lines = mutableListOf<Line>().apply {
                var currentParagraph: Line? = null

                page.lines.forEach { line ->
                    if (isNewParagraphStart(line)) {
                        currentParagraph?.let { add(it) }
                        currentParagraph = line
                    } else currentParagraph = processLineStartsLowerCase(currentParagraph, line)
                }

                currentParagraph?.let { add(it) }
            }
        }
    }

    /**
     * Method checks when current line should to be a start for a new paragraph.
     * @param line current line
     * @return true when the current line should to be a new paragraph, false when not
     */
    private fun isNewParagraphStart(line: Line): Boolean =
        isUpperCase(line.text) || isDigit(line.text) || isSymbol(line.text)

    /**
     * Often some paragraphs start with symbol like a '-' etc.
     * This method checks if the first letter is not a letter.
     * It is unnecessary to check if it is a digit that does the method 'isDigit(…)'
     * @param text current line
     * @return true when digit and false when not
     */
    private fun isSymbol(text: String?): Boolean = text?.firstOrNull()?.isLetter()?.not() == true

    /**
     * Often chapters starts with a digit. This method checks if the first letter is a digit.
     * @param text current line
     * @return true when digit and false when not
     */
    private fun isDigit(text: String?): Boolean = text?.firstOrNull()?.isDigit() == true

    /**
     * Method shows when currentGroup is null, then returns line immediately without transformations,
     * when currentGroup is not null, then currentGroup will be concatenated with new line
     * @param currentParagraph the current line holder
     * @param line current line
     * @return a new value for a currentGroup
     */
    private fun processLineStartsLowerCase(currentParagraph: Line?, line: Line): Line =
        currentParagraph?.copy(text = "${currentParagraph.text} ${line.text}") ?: line

    /**
     * Method checks: is the first letter in the upper case.
     * @param text one line
     * @return true when in upper case and false when not
     */
    private fun isUpperCase(text: String?): Boolean = text?.firstOrNull()?.isUpperCase() == true
}
