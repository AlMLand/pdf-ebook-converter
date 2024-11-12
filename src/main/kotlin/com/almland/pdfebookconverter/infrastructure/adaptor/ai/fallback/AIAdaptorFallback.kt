package com.almland.pdfebookconverter.infrastructure.adaptor.ai.fallback

import org.slf4j.LoggerFactory

internal open class AIAdaptorFallback {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private val FALLBACK_VALUE = emptyList<String>()
        private const val ERROR_MESSAGE = "Service is down or overloaded, request with argument \"{}\" was not took, " +
                "error message: {}"
    }

    protected fun callFallback(text: String, throwable: Throwable): Collection<String> =
        FALLBACK_VALUE.also { logger.error(ERROR_MESSAGE, text, throwable.message) }
}
