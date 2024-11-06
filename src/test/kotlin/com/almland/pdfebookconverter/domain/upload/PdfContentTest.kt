package com.almland.pdfebookconverter.domain.upload

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource
import org.springframework.test.util.ReflectionTestUtils
import java.awt.image.BufferedImage

internal class PdfContentTest {

    companion object {
        private const val PDF_TEXT_TEST_PDF = "pdf-text-test.pdf"
        private const val PDF_TEXT_IMAGE_TEST_PDF = "pdf-text-image-test.pdf"
        private const val PDF_THREE_IMAGES_TEST_PDF = "pdf-three-images-test.pdf"
    }

    @Test
    fun `extractText, actual is equal to expected text`() {
        val expected = "Dummy PDF file"

        val actual = ReflectionTestUtils.invokeMethod<String>(
            PdfContent(ClassPathResource(PDF_TEXT_TEST_PDF).inputStream),
            "extractText"
        )

        assertThat(actual).isNotNull
        assertThat(actual!!.trim()).isEqualTo(expected)
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

        val actual = ReflectionTestUtils.invokeMethod<Map<Int, MutableMap<Int, BufferedImage>>>(
            PdfContent(ClassPathResource(PDF_THREE_IMAGES_TEST_PDF).inputStream),
            "extractImages"
        )

        assertThat(actual).isNotNull
        assertThat(actual!!.size).isEqualTo(expectedPageToOnPageIndexToImageSize)
        assertThat(actual[firstPageWitchContainsImage]!!.size).isEqualTo(expectedFirstOnPageIndexToImageSize)
        assertThat(actual[secondPageWitchContainsImage]!!.size).isEqualTo(expectedSecondOnPageIndexToImageSize)
    }

    @Test
    fun `after init from PdfContent instance, text and images variables in the instance are correct`() {
        val pageIndex = 0

        val expectedPageToOnPageIndexToImageSize = 1
        val expectedImagesSize = 1
        val expectedText = "This is a test"

        val pdfContent = PdfContent(ClassPathResource(PDF_TEXT_IMAGE_TEST_PDF).inputStream)

        with(pdfContent) {
            assertThat(text.trim()).isEqualTo(expectedText)
            assertThat(images.size).isEqualTo(expectedPageToOnPageIndexToImageSize)
            assertThat(images[pageIndex]!!.size).isEqualTo(expectedImagesSize)
        }
    }
}
