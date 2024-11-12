package com.almland.pdfebookconverter.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry

@EnableRetry
@Configuration
internal class RetryConfiguration
