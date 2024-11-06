package com.almland.pdfebookconverter.application.aggregate.creator.fb2

import com.almland.pdfebookconverter.domain.upload.PdfContent
import org.w3c.dom.Document
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Base64
import javax.imageio.ImageIO
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

internal class FB2Creator {

    fun createFB2(pdfContent: PdfContent) {
        getNewDocument().let { document ->
            val root = document.createElement("root").apply {
                setAttribute("xmlns:l", "http://www.w3.org/1999/xlink")
                document.appendChild(this)
            }
            val body = document.createElement("body").apply { root.appendChild(this) }
            val section = document.createElement("section").apply {
                appendChild(document.createElement("p").apply { textContent = pdfContent.text })
                body.appendChild(this)
            }

            pdfContent.images.forEach { pageIndex, indexOnPageToImages ->
                indexOnPageToImages.forEach { indexOnPage, image ->
                    document.createElement("binary").apply {

                        document.createElement("p").apply {
                            appendChild(
                                document.createElement("image")
                                    .apply { setAttribute("l:href", "#image_$pageIndex-$indexOnPage") })
                            section.appendChild(this)
                        }
                        document.createElement("binary").apply {
                            setAttribute("id", "image_$pageIndex-$indexOnPage")
                            setAttribute("content-type", "image/jpeg")
                            appendChild(document.createTextNode(convertImageToBase64(image)))
                            root.appendChild(this)
                        }
                    }
                }
            }

            val transformer = TransformerFactory.newInstance().newTransformer()
            val source = DOMSource(document)
            val target = StreamResult(File("src/main/resources/newFile.fb2"))
            transformer.transform(source, target)
        }
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
