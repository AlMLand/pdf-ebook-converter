package com.almland.pdfebookconverter.infrastructure.configuration

import com.almland.pdfebookconverter.application.aggregate.PdfDocumentAggregator
import com.almland.pdfebookconverter.application.aggregate.creator.epub.EPUBCreator
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Creator
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import com.almland.pdfebookconverter.application.port.creator.Creator
import com.almland.pdfebookconverter.application.port.outbound.AIPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class PdfDocumentAggregatorConfiguration {

    @Bean("fB2Creator")
    fun fB2Creator(): Creator = FB2Creator()

    @Bean("ePUBCreator")
    fun ePUB2Creator(): Creator = EPUBCreator()

    @Bean
    fun pdfDocumentAggregator(
        aiPort: AIPort,
        @Qualifier("fB2Creator") fB2Creator: Creator,
        @Qualifier("ePUBCreator") ePUBCreator: Creator
    ): AggregateQueryPort = PdfDocumentAggregator(aiPort, fB2Creator, ePUBCreator)
}
