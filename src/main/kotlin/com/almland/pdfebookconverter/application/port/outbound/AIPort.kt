package com.almland.pdfebookconverter.application.port.outbound

internal interface AIPort {
    fun call(text: String): Collection<String>
}
