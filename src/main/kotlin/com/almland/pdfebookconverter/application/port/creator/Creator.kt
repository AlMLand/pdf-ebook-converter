package com.almland.pdfebookconverter.application.port.creator

import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

internal interface Creator {
    suspend fun create(pdfDocument: PdfDocument, context: CoroutineContext): InputStream
}
