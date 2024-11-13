package com.almland.pdfebookconverter.application.aggregate.creator.fb2

import com.almland.pdfebookconverter.domain.PdfDocument
import org.junit.jupiter.api.Test
import org.springframework.core.io.ClassPathResource

internal class FB2CreatorTest {

    companion object {
        private const val PDF_TEXT_TEST_PDF = "pdf-text-test.pdf"
        private const val PDF_TEXT_IMAGE_TEST_PDF = "pdf-text-image-test.pdf"
        private const val PDF_THREE_IMAGES_TEST_PDF = "pdf-three-images-test.pdf"
    }

    @Test
    fun `createFB2 `() {
        FB2Creator().create(PdfDocument(FileTarget.FB2, ClassPathResource(PDF_TEXT_IMAGE_TEST_PDF).inputStream))
    }
}
