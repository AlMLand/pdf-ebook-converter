package com.almland.pdfebookconverter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

internal class PdfContentTest {

    companion object {
        private const val PDF_TEXT_IMAGE_TEST_PDF = "pdf-text-image-test.pdf"
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
