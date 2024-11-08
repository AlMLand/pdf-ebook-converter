package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.domain.PdfContentBuilder.extractDescription
import com.almland.pdfebookconverter.domain.PdfContentBuilder.extractPages
import java.io.InputStream

internal data class PdfContent(private val content: InputStream) {

    private val bufferedContent = content.readAllBytes()
    val pages: Collection<Page> by lazy { extractPages(bufferedContent) }
    val description: Description by lazy { extractDescription(bufferedContent) }
}
