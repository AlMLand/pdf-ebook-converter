package com.almland.pdfebookconverter.application.aggregate.creator

import com.almland.pdfebookconverter.domain.PdfContent
import java.io.InputStream

internal interface Creator {
    fun create(pdfContent: PdfContent): InputStream
}