package com.almland.pdfebookconverter.application.port.aggregator

import java.io.InputStream

internal interface AggregateQueryPort {
    fun getSuggestions(fileName: String): Collection<String>
    fun create(fileName: String, target: String, content: InputStream): InputStream
}
