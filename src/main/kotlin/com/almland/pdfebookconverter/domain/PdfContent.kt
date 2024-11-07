package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.domain.PdfContentBuilderIntern.extractImages
import com.almland.pdfebookconverter.domain.PdfContentBuilderIntern.extractText
import java.awt.image.BufferedImage
import java.io.InputStream

internal data class PdfContent(val content: InputStream) {

    private val bufferedContent = content.readAllBytes()
    val text: String by lazy { extractText(bufferedContent) }
    val images: Map<Int, MutableMap<Int, BufferedImage>> by lazy { extractImages(bufferedContent) }
}
