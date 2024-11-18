package com.almland.pdfebookconverter.application.port.outbound

internal interface AIPort {
    suspend fun call(text: String): Collection<String>
}
