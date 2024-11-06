package com.almland.pdfebookconverter.application.aggregate

import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Creator
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.domain.upload.PdfContent
import java.io.InputStream

internal class ResultContentAggregator(private val fB2Creator: FB2Creator) : AggregateQueryPort {

    override fun uploadPdf(content: InputStream) {
        fB2Creator.createFB2(PdfContent(content))
    }
}
