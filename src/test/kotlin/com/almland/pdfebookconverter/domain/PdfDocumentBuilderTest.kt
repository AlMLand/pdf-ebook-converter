package com.almland.pdfebookconverter.domain

import com.almland.pdfebookconverter.application.aggregate.expractor.PdfDocumentExtractor
import java.awt.image.BufferedImage
import java.util.stream.Stream
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.core.io.ClassPathResource
import org.springframework.test.util.ReflectionTestUtils

internal class PdfDocumentBuilderTest {

    companion object {
        private const val PDF_TEXT_TEST_PDF = "pdf-text-test.pdf"
        private const val PDF_TEXT_IMAGE_TEST_PDF = "pdf-text-image-test.pdf"
        private const val PDF_THREE_IMAGES_TEST_PDF = "pdf-three-images-test.pdf"

        @JvmStatic
        fun getTestDataExtractPages(): Stream<Arguments> = Stream.of(
            arguments(PDF_TEXT_IMAGE_TEST_PDF, 1), arguments(PDF_THREE_IMAGES_TEST_PDF, 4)
        )

        @JvmStatic
        fun getTestDataExtractText(): Stream<Arguments> = Stream.of(
            arguments(PDF_TEXT_TEST_PDF, "Dummy PDF file"), arguments(PDF_TEXT_IMAGE_TEST_PDF, "This is a test")
        )

        @JvmStatic
        fun getTestDataExtractImages(): Stream<Arguments> = Stream.of(
            arguments(0, 1), arguments(1, 0), arguments(2, 2)
        )
    }

    @Test
    fun `extractDescription, PDF file has author but no title`() {
//        val expectedFirstname = "Evangelos"
//        val expectedLastname = "Vlachogiannis"
//        val expectedTitle = ""
//
//        val content = ClassPathResource(PDF_TEXT_TEST_PDF).contentAsByteArray
//
//        val actual = PdfDocumentExtractor.extractDescription(content)
//
//        with(actual) {
//            assertThat(author.firstName).isEqualTo(expectedFirstname)
//            assertThat(author.lastName).isEqualTo(expectedLastname)
//            assertThat(title).isEqualTo(expectedTitle)
//        }
    }

    /**
     * scenario 1 -> extracted size is 1
     * scenario 2 -> extracted size is 4
     */
    @ParameterizedTest
    @MethodSource("getTestDataExtractPages")
    fun `extractPages, scenario describe above`(file: String, expectedSize: Int) {
//        val content = ClassPathResource(file).contentAsByteArray
//
//        val actual = PdfDocumentExtractor.extractPages(content)
//
//        assertThat(actual.size).isEqualTo(expectedSize)
    }

    /**
     * scenario 1 -> PDF contains only text
     * scenario 2 -> PDF contains text and image
     */
    @ParameterizedTest
    @MethodSource("getTestDataExtractText")
    fun `extractText, scenario describe above`(file: String, expectedText: String) {
        val pageIndex = 0
        val pdfDocument = Loader.loadPDF(ClassPathResource(file).file)
        val textStripper = PDFTextStripper().apply { sortByPosition = true; addMoreFormatting = true }

        val actual = ReflectionTestUtils.invokeMethod<String>(
            PdfDocumentExtractor,
            "extractText",
            pdfDocument,
            textStripper,
            pageIndex
        )

        assertThat(actual).isNotNull
        assertThat(actual!!.trim()).isEqualTo(expectedText)
    }

    /**
     * the PDF file has 4 pages,
     * page 1 contains 1 picture
     * page 2 don't have any pictures
     * page 3 contains 2 pictures
     * page 4 don't have any pictures
     *
     * scenario 1 -> for pageIndex 0 returns map with size 1
     * scenario 2 -> for pageIndex 1 returns map with size 0
     * scenario 3 -> for pageIndex 2 returns map with size 2
     */
    @ParameterizedTest
    @MethodSource("getTestDataExtractImages")
    fun `extractImages, scenario described above`(pageIndex: Int, expectedSize: Int) {
        val pdfDocument = Loader.loadPDF(ClassPathResource(PDF_THREE_IMAGES_TEST_PDF).file)

        val actual = ReflectionTestUtils.invokeMethod<Map<Int, BufferedImage>>(
            PdfDocumentExtractor,
            "extractImages",
            pdfDocument,
            pageIndex
        )

        assertThat(actual).isNotNull
        assertThat(actual!!.size).isEqualTo(expectedSize)
    }
}