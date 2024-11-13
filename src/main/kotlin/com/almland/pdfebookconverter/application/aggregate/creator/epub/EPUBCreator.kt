package com.almland.pdfebookconverter.application.aggregate.creator.epub

import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream

internal class EPUBCreator : Creator {

    override fun create(pdfDocument: PdfDocument): InputStream {
        TODO("Not yet implemented")
    }
}
