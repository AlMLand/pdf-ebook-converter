package com.almland.pdfebookconverter.application.aggregate.creator.epub

import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

internal open class EPUBCreator : Creator {

    override suspend fun create(pdfDocument: PdfDocument, context: CoroutineContext): InputStream {
        TODO("Not yet implemented")
    }
}
