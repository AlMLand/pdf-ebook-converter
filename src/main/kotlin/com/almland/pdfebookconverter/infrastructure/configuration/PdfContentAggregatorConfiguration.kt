package com.almland.pdfebookconverter.infrastructure.configuration

import com.almland.pdfebookconverter.application.aggregate.PdfContentAggregator
import com.almland.pdfebookconverter.application.aggregate.creator.Creator
import com.almland.pdfebookconverter.application.aggregate.creator.fb2.FB2Creator
import com.almland.pdfebookconverter.application.port.aggregator.AggregateQueryPort
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class PdfContentAggregatorConfiguration {

    @Bean
    fun fB2Creator(): Creator = FB2Creator()

    @Bean
    fun pdfContentAggregator(@Qualifier("fB2Creator") fB2Creator: Creator): AggregateQueryPort =
        PdfContentAggregator(fB2Creator)
}
