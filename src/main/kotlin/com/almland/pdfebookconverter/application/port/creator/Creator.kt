package com.almland.pdfebookconverter.application.port.creator

import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream

internal interface Creator {
    fun create(pdfDocument: PdfDocument): InputStream
}