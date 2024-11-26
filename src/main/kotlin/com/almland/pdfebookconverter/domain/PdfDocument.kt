package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.domain.metainfo.Description
import com.almland.pdfebookconverter.domain.structure.Page

internal data class PdfDocument(val pages: Collection<Page>, val description: Description)
