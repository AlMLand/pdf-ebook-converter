package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.domain.PdfDocumentBuilder.extractDescription
import com.almland.pdfebookconverter.domain.PdfDocumentBuilder.extractPages
import java.io.InputStream

internal data class PdfDocument(private val content: InputStream) {

    private val bufferedContent = content.readAllBytes()
    val pages: Collection<Page> by lazy { extractPages(bufferedContent) }
    val description: Description by lazy { extractDescription(bufferedContent) }
}
