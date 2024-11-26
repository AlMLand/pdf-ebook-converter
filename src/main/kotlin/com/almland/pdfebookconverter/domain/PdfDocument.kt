package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.domain.pdffilestructure.Page
import com.almland.pdfebookconverter.domain.pdfmetainfo.Description

internal data class PdfDocument(val pages: Collection<Page>, val description: Description)
