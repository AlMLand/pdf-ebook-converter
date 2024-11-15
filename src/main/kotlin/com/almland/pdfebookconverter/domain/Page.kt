package com.almland.pdfebookconverter.domain

import java.awt.image.BufferedImage

internal data class Page(
    val index: Int,
    var lines: MutableCollection<Line>,
    var images: Map<Int, BufferedImage>
)
