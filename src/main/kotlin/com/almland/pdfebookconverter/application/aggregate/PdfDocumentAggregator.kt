package com.almland.pdfebookconverter.application.aggregate

import com.almland.pdfebookconverter.application.aggregate.creator.rule.PdfDocumentRule
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.application.port.coroutines.CustomScope
import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.application.port.outbound.AIPort
import java.io.InputStream
import kotlinx.coroutines.withContext

internal open class PdfDocumentAggregator(
    private val aiPort: AIPort,
    private val fB2Creator: Creator,
    private val ePUBCreator: Creator
) : AggregateQueryPort {

    override suspend fun getSuggestions(fileName: String): Collection<String> = aiPort.call(fileName)

    override suspend fun create(
        fileName: String, target: String, content: InputStream, coroutineScope: CustomScope,
    ): InputStream = withContext(coroutineScope.coroutineContext) {
        PdfDocumentRule().create(fB2Creator, ePUBCreator, content, target)
    }
}
