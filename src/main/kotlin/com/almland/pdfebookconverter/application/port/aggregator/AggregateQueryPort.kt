package com.almland.pdfebookconverter.application.port.aggregator

import java.io.InputStream
import kotlin.coroutines.CoroutineContext

internal interface AggregateQueryPort {
    suspend fun getSuggestions(fileName: String, context: CoroutineContext): Collection<String>
    suspend fun create(target: String, fileName: String, content: InputStream, context: CoroutineContext): InputStream
}
