package com.almland.pdfebookconverter.application.port.aggregator

import java.io.InputStream

internal interface AggregateQueryPort {
    fun createFB2(content: InputStream): InputStream
}
