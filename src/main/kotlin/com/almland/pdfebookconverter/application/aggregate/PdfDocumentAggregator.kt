package com.almland.pdfebookconverter.application.aggregate

import com.almland.pdfebookconverter.application.aggregate.creator.Creator
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.application.port.outbound.AIPort
import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream

internal open class PdfDocumentAggregator(
    private val aiPort: AIPort,
    private val creator: Creator
) : AggregateQueryPort {

    override fun getSuggestions(fileName: String): Collection<String> =
        aiPort.call(fileName)

    override fun create(fileName: String, target: String, content: InputStream): InputStream =
        creator.create(PdfDocument(content))
}
