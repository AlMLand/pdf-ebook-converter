package com.almland.pdfebookconverter.domain.structure

import java.awt.image.BufferedImage

internal data class Image(
    val sequenceOrder: Int,
    var bufferedImage: BufferedImage
)
