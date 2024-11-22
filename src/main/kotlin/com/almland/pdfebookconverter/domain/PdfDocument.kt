package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.application.aggregate.expractor.PDFExtractor.extractDescription
import com.almland.pdfebookconverter.application.aggregate.expractor.PDFExtractor.extractPages
import com.almland.pdfebookconverter.domain.pdffilestructure.Page
import com.almland.pdfebookconverter.domain.pdfmetainfo.Description
import java.io.InputStream

internal data class PdfDocument(private val content: InputStream) {

    private val bufferedContent = content.readAllBytes()
    val pages: Collection<Page> by lazy { extractPages(bufferedContent) }
    val description: Description by lazy { extractDescription(bufferedContent) }
}
