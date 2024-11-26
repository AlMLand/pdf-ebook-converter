package com.almland.pdfebookconverter.application.aggregate

import com.almland.pdfebookconverter.application.aggregate.expractor.PdfDocumentExtractor
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.application.port.outbound.AIPort
import com.almland.pdfebookconverter.domain.FileTarget.FB2
import java.io.InputStream
import kotlin.coroutines.CoroutineContext

internal open class PdfDocumentAggregator(
    private val aiPort: AIPort,
    private val fB2Creator: Creator,
    private val ePUBCreator: Creator
) : AggregateQueryPort {

    override suspend fun getSuggestions(fileName: String, context: CoroutineContext): Collection<String> =
        aiPort.call(fileName, context)

    override suspend fun create(
        target: String, fileName: String, content: InputStream, context: CoroutineContext
    ): InputStream = PdfDocumentExtractor.create(content, context).let { pdfDocument ->
        if (target == FB2.target) fB2Creator.create(pdfDocument, context)
        else ePUBCreator.create(pdfDocument, context)
    }
}
