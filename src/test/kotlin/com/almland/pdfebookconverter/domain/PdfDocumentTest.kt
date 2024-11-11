package com.almland.pdfebookconverter.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

internal class PdfDocumentTest {

    companion object {
        private const val PDF_TEXT_IMAGE_TEST_PDF = "pdf-text-image-test.pdf"
    }

    @Test
    fun `after init from PdfDocument instance, variables in the instance are correct`() {
        val expectedKey = 0
        val expectedSize = 1
        val expectedPageIndex = 0
        val expectedText = "This is a test"
        val expectedTitle = ""
        val expectedFirstName = "Alex"
        val expectedLastname = "Morland"

        val actual = PdfDocument(ClassPathResource(PDF_TEXT_IMAGE_TEST_PDF).inputStream)

        assertThat(actual.description.title).isEqualTo(expectedTitle)
        assertThat(actual.description.author.firstName).isEqualTo(expectedFirstName)
        assertThat(actual.description.author.lastName).isEqualTo(expectedLastname)
        assertThat(actual.pages.size).isEqualTo(expectedSize)
        with((actual.pages as List)[0]) {
            assertThat(pageIndex).isEqualTo(expectedPageIndex)
            assertThat(text.trim()).isEqualTo(expectedText)
            assertThat(images.keys.size).isEqualTo(expectedSize)
            assertThat(images.keys).containsOnly(expectedKey)
        }
    }
}
