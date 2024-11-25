package com.almland.pdfebookconverter.application.aggregate.coroutines

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.slf4j.LoggerFactory

internal class CustomScope(private val onThrowable: () -> Unit) : CoroutineScope {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val parentJob = Job()
    private val loggingInterceptor = LoggingInterceptor()
    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        onThrowable()
        parentJob.cancel()
        logger.error("Coroutine parent job canceled after exception {}{}", throwable, getSuppressed(throwable))
    }

    override val coroutineContext: CoroutineContext
        get() = parentJob + loggingInterceptor + exceptionHandler

    fun onStop() {
        parentJob.cancel()
    }

    private fun getSuppressed(throwable: Throwable): String =
        throwable.suppressed
            .contentToString()
            .takeIf { suppressed -> suppressed.length > 2 }
            ?.let { suppressed -> " with suppressed $suppressed" }
            ?: ""
}
