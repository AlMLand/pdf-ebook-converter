package com.almland.pdfebookconverter.domain

internal data class Page(
    val index: Int,
    var lines: MutableCollection<Line>,
    var images: Collection<Image>
)
