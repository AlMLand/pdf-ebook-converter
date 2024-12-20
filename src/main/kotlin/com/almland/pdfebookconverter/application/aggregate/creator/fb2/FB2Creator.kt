package com.almland.pdfebookconverter.application.aggregate.creator.fb2

import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.AUTHOR
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.BINARY
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.BODY
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.BOOK_TITLE
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.DESCRIPTION
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.DOCUMENT_INFO
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.FIRST_NAME
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.IMAGE
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.LAST_NAME
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.NICKNAME
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.PARAGRAPH
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.PROGRAM_USED
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.ROOT
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.SECTION
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.STRONG
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.TITLE_INFO
import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.domain.PdfDocument
import com.almland.pdfebookconverter.domain.structure.Image
import com.almland.pdfebookconverter.domain.structure.Line
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys.ENCODING
import javax.xml.transform.OutputKeys.INDENT
import javax.xml.transform.OutputKeys.STANDALONE
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Document
import org.w3c.dom.Element

internal open class FB2Creator : Creator {

    companion object {
        private const val CONTENT_TYPE = "png"
        private const val APP_NAME = "T converter"
        private const val XLINK_NAMESPACE_KEY = "xmlns:l"
        private const val XLINK_NAMESPACE_VALUE = "http://www.w3.org/1999/xlink"
    }

    private lateinit var root: Element
    private lateinit var body: Element
    private lateinit var section: Element

    /**
     * @param pdfDocument domain object
     * @return create an inputStream from document, a document will be created from PdfDocument
     * document can contain text and images
     */
    override suspend fun create(pdfDocument: PdfDocument, context: CoroutineContext): InputStream =
        withContext(context + Dispatchers.Default) {
            getNewDocument().let { document ->
                document.createElement(ROOT.tag).apply {
                    setAttribute(XLINK_NAMESPACE_KEY, XLINK_NAMESPACE_VALUE)
                    document.appendChild(this)
                    root = this
                }
                addDocumentDescription(pdfDocument, document, coroutineContext)
                document.createElement(BODY.tag).apply {
                    root.appendChild(this)
                    body = this
                }
                document.createElement(SECTION.tag).apply {
                    body.appendChild(this)
                    section = this
                }

                fillDocument(pdfDocument, document, coroutineContext)

                documentToInputStream(document)
            }
        }

    /**
     * Add the document description like title, author first and last name
     * @param document this object is a framework witch will in this case contain common information over a book
     */
    private suspend fun addDocumentDescription(
        pdfDocument: PdfDocument,
        document: Document,
        context: CoroutineContext
    ) {
        withContext(context) {
            document.createElement(DESCRIPTION.tag).also { description ->

                document.createElement(DOCUMENT_INFO.tag).also { documentInfo ->
                    document.createElement(AUTHOR.tag).also { author ->
                        document.createElement(NICKNAME.tag).apply {
                            textContent = with(pdfDocument.description.author) { "$firstName $lastName" }
                            author.appendChild(this)
                        }
                        documentInfo.appendChild(author)
                    }
                    document.createElement(PROGRAM_USED.tag).apply {
                        textContent = APP_NAME
                        documentInfo.appendChild(this)
                    }
                    description.appendChild(documentInfo)
                }

                document.createElement(TITLE_INFO.tag).also { titleInfo ->
                    document.createElement(BOOK_TITLE.tag).apply {
                        textContent = pdfDocument.description.title
                        titleInfo.appendChild(this)
                    }
                    document.createElement(AUTHOR.tag).also { author ->
                        document.createElement(FIRST_NAME.tag).apply {
                            textContent = pdfDocument.description.author.firstName
                            author.appendChild(this)
                        }
                        document.createElement(LAST_NAME.tag).apply {
                            textContent = pdfDocument.description.author.lastName
                            author.appendChild(this)
                        }
                        titleInfo.appendChild(author)
                    }
                    description.appendChild(titleInfo)
                }

                root.appendChild(description)
            }
        }
    }

    /**
     * Inserts all text lines first time after this all images into a document.
     * @param pdfDocument domain object
     * @param document this object is a framework witch will contain text and images
     */
    private suspend fun fillDocument(pdfDocument: PdfDocument, document: Document, context: CoroutineContext) {
        withContext(context) {
            pdfDocument.pages.forEach { page ->
                with(page) {
                    images.forEach { insertImage(document, it, context) }
                    lines.forEach { insertText(document, it, context) }
                }
            }
        }
    }

    private suspend fun insertText(document: Document, line: Line, context: CoroutineContext) {
        withContext(context) {
            if (line.isBold == true) {
                document.createElement(PARAGRAPH.tag).also { paragraph ->
                    document.createElement(STRONG.tag).apply {
                        textContent = line.text
                        paragraph.appendChild(this)
                    }
                    section.appendChild(paragraph)
                }
            } else document.createElement(PARAGRAPH.tag).apply {
                textContent = line.text
                section.appendChild(this)
            }
        }
    }

    /**
     * Insert images into a document. It's possible for one or more images per page.
     * @param document this object is a framework that will contain images
     * @param image domain object image
     */
    private suspend fun insertImage(document: Document, image: Image, context: CoroutineContext) {
        withContext(context) {
            document.createElement(BINARY.tag).apply {
                document.createElement(PARAGRAPH.tag).apply {
                    appendChild(
                        document.createElement(IMAGE.tag).apply {
                            setAttribute("l:href", "#image_${image.hashCode()}-${image.sequenceOrder}")
                        }
                    )
                    section.appendChild(this)
                }
                document.createElement(BINARY.tag).apply {
                    setAttribute("id", "image_${image.hashCode()}-${image.sequenceOrder}")
                    setAttribute("content-type", "image/$CONTENT_TYPE")
                    appendChild(document.createTextNode(convertImageToBase64(image.bufferedImage)))
                    root.appendChild(this)
                }
            }
        }
    }

    /**
     * @param document this object is a framework witch will contain text and images
     * @return  convert a document to the input stream and returns this
     */
    private fun documentToInputStream(document: Document): InputStream =
        ByteArrayOutputStream().let {
            getTransformFactory()
                .transform(DOMSource(document), StreamResult(it))
            ByteArrayInputStream(it.toByteArray())
        }

    /**
     * @return xml transformer
     */
    private fun getTransformFactory(): Transformer =
        TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(INDENT, "yes")
            setOutputProperty(ENCODING, "UTF-8")
            setOutputProperty(STANDALONE, "no")
        }

    /**
     * @return new document instance
     */
    private fun getNewDocument(): Document =
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .newDocument()

    /**
     * @param bufferedImage buffered image
     * @return buffered image will be encoded and returns as string
     */
    private fun convertImageToBase64(bufferedImage: BufferedImage): String =
        ByteArrayOutputStream().let {
            ImageIO.write(bufferedImage, CONTENT_TYPE, it)
            Base64
                .getEncoder()
                .encodeToString(it.toByteArray())
        }
}
