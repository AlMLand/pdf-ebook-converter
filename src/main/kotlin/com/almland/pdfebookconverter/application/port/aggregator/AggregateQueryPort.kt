package com.almland.pdfebookconverter.application.port.aggregator

import java.io.InputStream

internal interface AggregateQueryPort {
    fun create(target: String, content: InputStream): InputStream
}
