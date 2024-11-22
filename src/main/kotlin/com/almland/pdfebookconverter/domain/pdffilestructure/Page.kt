package com.almland.pdfebookconverter.domain.pdffilestructure

internal data class Page(
    val index: Int,
    var lines: MutableCollection<Line>,
    var images: Collection<Image>
)
