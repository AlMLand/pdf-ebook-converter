package com.almland.pdfebookconverter.application.aggregate

import com.almland.pdfebookconverter.application.aggregate.creator.rule.PdfDocumentRule
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.application.port.outbound.AIPort
import java.io.InputStream

internal open class PdfDocumentAggregator(
    private val aiPort: AIPort,
    private val fB2Creator: Creator,
    private val ePUBCreator: Creator
) : AggregateQueryPort {

    override fun getSuggestions(fileName: String): Collection<String> =
        aiPort.call(fileName)

    override fun create(fileName: String, target: String, content: InputStream): InputStream =
        PdfDocumentRule().create(fB2Creator, ePUBCreator, content, target)
}
