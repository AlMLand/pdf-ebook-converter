package com.almland.pdfebookconverter.application.aggregate.coroutines

import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.isActive
import org.slf4j.LoggerFactory

internal class LoggingInterceptor : ContinuationInterceptor {

    override val key: CoroutineContext.Key<*>
        get() = ContinuationInterceptor

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> =
        LoggingContinuation(continuation)

    private class LoggingContinuation<T>(private val continuation: Continuation<T>) : Continuation<T> {

        private val logger = LoggerFactory.getLogger(this::class.java)

        override val context: CoroutineContext
            get() = continuation.context

        override fun resumeWith(result: Result<T>) {
            if (context.isActive.not()) {
                logger.info("Current thread: {}, Coroutine cancelled, not resuming.", Thread.currentThread().name)
                return
            }

            logger.info("Current thread: {}, resuming.", Thread.currentThread().name)
            continuation.resumeWith(result)
        }
    }
}
