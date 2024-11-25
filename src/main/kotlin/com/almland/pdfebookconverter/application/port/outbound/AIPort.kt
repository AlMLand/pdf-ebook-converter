package com.almland.pdfebookconverter.application.port.outbound

import kotlin.coroutines.CoroutineContext

internal interface AIPort {
    suspend fun call(text: String, context: CoroutineContext): Collection<String>
}
