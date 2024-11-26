package com.almland.pdfebookconverter.domain.structure

internal data class Line(
    var text: String?,
    val fontName: String,
    var isBold: Boolean? = null
) {
    init {
        text = normalizeDots(text)
        isBold = isBold(fontName)
    }

    companion object {
        private const val POINT = '.'
        private const val MAX_POINT_COUNT = 10
        private const val POINTS_REPLACEMENT = ""
        private val REGEX_POINT_SEQUENCE = Regex("\\. {1,10}|\\.{1,10}")
        private const val FONT_PATTERN_BOLD = "bold"
        private const val FONT_PATTERN_DEMI = "demi"
        private const val FONT_PATTERN_BLACK = "black"
    }

    private fun normalizeDots(text: String?): String? =
        text?.let { deleteDots(it) }

    /**
     * Delete unnecessary dots in the line.
     * When the count of dots is more than 10, the text will be returned,
     * by one time 20 dots will be deleted.
     * @param text current line
     * @return current line text, with less than 10 points
     */
    private fun deleteDots(text: String): String =
        if (containsLessThenTenPoints(text)) text
        else deleteDots(text.replaceFirst(REGEX_POINT_SEQUENCE, POINTS_REPLACEMENT))

    /**
     * Check for text contains less than 10 points.
     * @param text current line
     * @return boolean
     */
    private fun containsLessThenTenPoints(text: String): Boolean =
        text.count { it == POINT } <= MAX_POINT_COUNT

    /**
     * Extract the first TextPosition from a collection, get font, and compare.
     * @param fontName collection of TextPositions
     */
    private fun isBold(fontName: String): Boolean =
        fontName
            .lowercase()
            .let { containsBoldPattern(it) } == true

    /**
     * Checks whe the actual fontName contains the key words.
     * @param fontName current fontName
     * @return boolean result
     */
    private fun containsBoldPattern(fontName: String): Boolean =
        with(fontName) { contains(FONT_PATTERN_BOLD) || contains(FONT_PATTERN_DEMI) || contains(FONT_PATTERN_BLACK) }
}
