package com.almland.pdfebookconverter.infrastructure.adaptor.ai.fallback

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import org.slf4j.LoggerFactory

internal open class AIAdaptorFallback {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private val FALLBACK_VALUE = emptyList<String>()
        private const val ERROR_MESSAGE = "Service is down, overloaded or job was cancelled, " +
                "request with argument \"{}\" was not took, error message: {}"
    }

    protected fun callFallback(
        text: String,
        context: CoroutineContext,
        continuation: Continuation<Any>,
        throwable: Throwable
    ): Collection<String> = FALLBACK_VALUE.also { logger.error(ERROR_MESSAGE, text, throwable.message) }
}
