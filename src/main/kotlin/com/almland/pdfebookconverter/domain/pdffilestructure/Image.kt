package com.almland.pdfebookconverter.domain.pdffilestructure

import java.awt.image.BufferedImage

internal data class Image(
    val order: Int,
    var bufferedImage: BufferedImage
)
