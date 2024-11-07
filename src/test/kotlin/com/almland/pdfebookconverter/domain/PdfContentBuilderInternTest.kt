package com.almland.pdfebookconverter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

internal class PdfContentBuilderInternTest {

    companion object {
        private const val PDF_TEXT_TEST_PDF = "pdf-text-test.pdf"
        private const val PDF_THREE_IMAGES_TEST_PDF = "pdf-three-images-test.pdf"
    }

    @Test
    fun `extractText, actual is equal to expected text`() {
        val expected = "Dummy PDF file"

        val actual = PdfContentBuilderIntern.extractText(ClassPathResource(PDF_TEXT_TEST_PDF).contentAsByteArray)

        assertThat(actual).isNotNull
        assertThat(actual.trim()).isEqualTo(expected)
    }

    /**
     * the pdf file has 4 pages,
     * page 1 contains 1 picture
     * page 2 don't have any pictures
     * page 3 contains 2 pictures
     * page 4 don't have any pictures
     */
    @Test
    fun `extractImages, scenario describe above`() {
        val firstPageWitchContainsImage = 0
        val secondPageWitchContainsImage = 2

        val expectedPageToOnPageIndexToImageSize = 2
        val expectedFirstOnPageIndexToImageSize = 1
        val expectedSecondOnPageIndexToImageSize = 2

        val actual = PdfContentBuilderIntern.extractImages(ClassPathResource(PDF_THREE_IMAGES_TEST_PDF).contentAsByteArray)

        assertThat(actual).isNotNull
        assertThat(actual.size).isEqualTo(expectedPageToOnPageIndexToImageSize)
        assertThat(actual[firstPageWitchContainsImage]!!.size).isEqualTo(expectedFirstOnPageIndexToImageSize)
        assertThat(actual[secondPageWitchContainsImage]!!.size).isEqualTo(expectedSecondOnPageIndexToImageSize)
    }
}