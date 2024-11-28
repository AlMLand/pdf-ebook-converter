package com.almland.pdfebookconverter.infrastructure.adaptor.ui.dto

import java.io.InputStream

internal data class DownloadDTO(private val results: List<Any>) {
    val content: InputStream = results[1] as InputStream
    val suggestions: Collection<String> = results[0] as Collection<String>
}
