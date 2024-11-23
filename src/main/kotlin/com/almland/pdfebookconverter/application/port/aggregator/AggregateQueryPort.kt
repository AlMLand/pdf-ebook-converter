package com.almland.pdfebookconverter.application.port.aggregator

import com.almland.pdfebookconverter.application.aggregate.coroutines.CustomScope
import java.io.InputStream

internal interface AggregateQueryPort {
    suspend fun getSuggestions(fileName: String): Collection<String>
    suspend fun create(fileName: String, target: String, content: InputStream, coroutineScope: CustomScope): InputStream
}
