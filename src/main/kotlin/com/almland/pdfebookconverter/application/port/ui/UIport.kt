package com.almland.pdfebookconverter.application.port.ui

import java.io.InputStream

internal interface UIport {
    fun uploadPdf(): InputStream
}
