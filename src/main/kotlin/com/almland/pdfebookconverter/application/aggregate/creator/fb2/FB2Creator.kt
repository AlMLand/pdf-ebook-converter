package com.almland.pdfebookconverter.application.aggregate.creator.fb2

import com.almland.pdfebookconverter.application.aggregate.creator.Creator
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.BINARY
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.BODY
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.IMAGE
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.PARAGRAPH
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.ROOT
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Tag.SECTION
import com.almland.pdfebookconverter.domain.PdfContent
import org.w3c.dom.Document
import org.w3c.dom.Element
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

internal class FB2Creator : Creator {

    companion object {
        private const val XLINK_NAMESPACE_KEY = "xmlns:l"
        private const val XLINK_NAMESPACE_VALUE = "http://www.w3.org/1999/xlink"
    }

    override fun create(pdfContent: PdfContent): InputStream =
        getNewDocument().let { document ->
            val root = document.createElement(ROOT.tag).apply {
                setAttribute(XLINK_NAMESPACE_KEY, XLINK_NAMESPACE_VALUE)
                document.appendChild(this)
            }
            val body = document.createElement(BODY.tag).apply { root.appendChild(this) }
            val section = document.createElement(SECTION.tag).apply {
                appendChild(document.createElement(PARAGRAPH.tag).apply { textContent = pdfContent.text })
                body.appendChild(this)
            }

            insertImages(pdfContent, document, section, root)

            documentToInputStream(document)
        }

    private fun insertImages(pdfContent: PdfContent, document: Document, section: Element, root: Element) {
        pdfContent.images.forEach { pageIndex, indexOnPageToImages ->
            indexOnPageToImages.forEach { indexOnPage, image ->
                document.createElement(BINARY.tag).apply {
                    document.createElement(PARAGRAPH.tag).apply {
                        appendChild(
                            document.createElement(IMAGE.tag)
                                .apply { setAttribute("l:href", "#image_$pageIndex-$indexOnPage") })
                        section.appendChild(this)
                    }
                    document.createElement(BINARY.tag).apply {
                        setAttribute("id", "image_$pageIndex-$indexOnPage")
                        setAttribute("content-type", "image/png")
                        appendChild(document.createTextNode(convertImageToBase64(image)))
                        root.appendChild(this)
                    }
                }
            }
        }
    }

    private fun documentToInputStream(document: Document): InputStream =
        ByteArrayOutputStream().let {
            getTransformFactory()
                .transform(DOMSource(document), StreamResult(it))
            ByteArrayInputStream(it.toByteArray())
        }

    private fun getTransformFactory(): Transformer =
        TransformerFactory.newInstance().newTransformer().apply {
            setOutputProperty(INDENT, "yes");
            setOutputProperty(ENCODING, "UTF-8");
            setOutputProperty(STANDALONE, "no");
        }

    private fun getNewDocument(): Document =
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .newDocument()

    private fun convertImageToBase64(bufferedImage: BufferedImage): String =
        ByteArrayOutputStream().let {
            ImageIO
                .write(bufferedImage, "png", it)
            Base64
                .getEncoder()
                .encodeToString(it.toByteArray())
        }
}
