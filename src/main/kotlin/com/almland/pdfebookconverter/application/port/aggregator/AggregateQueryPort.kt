package com.almland.pdfebookconverter.application.port.aggregator

import java.io.InputStream

internal interface AggregateQueryPort {
    fun uploadPdf(content: InputStream)
}
