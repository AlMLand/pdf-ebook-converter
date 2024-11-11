package com.almland.pdfebookconverter.application.aggregate

import com.almland.pdfebookconverter.application.aggregate.creator.Creator
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.domain.PdfDocument
import java.io.InputStream

internal open class PdfDocumentAggregator(private val creator: Creator) : AggregateQueryPort {

    override fun create(target: String, content: InputStream): InputStream =
        creator.create(PdfDocument(content))
}
