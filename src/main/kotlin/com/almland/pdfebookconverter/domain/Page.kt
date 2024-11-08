package com.almland.pdfebookconverter.domain

import java.awt.image.BufferedImage

internal data class Page(
    val pageIndex: Int,
    val text: String,
    val images: Map<Int, BufferedImage>
)
