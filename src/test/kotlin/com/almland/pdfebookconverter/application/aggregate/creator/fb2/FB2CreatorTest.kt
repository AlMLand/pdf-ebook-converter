package com.almland.pdfebookconverter.application.aggregate.creator.fb2

import com.almland.pdfebookconverter.domain.upload.PdfContent
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
        FB2Creator().createFB2(PdfContent(ClassPathResource(PDF_THREE_IMAGES_TEST_PDF).inputStream))
    }
}