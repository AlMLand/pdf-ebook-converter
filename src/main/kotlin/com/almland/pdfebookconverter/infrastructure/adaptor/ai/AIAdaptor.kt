package com.almland.pdfebookconverter.infrastructure.adaptor.ai

import com.almland.pdfebookconverter.application.port.outbound.AIPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ai.fallback.AIAdaptorFallback
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
internal class AIAdaptor(private val chatClient: ChatClient) : AIPort, AIAdaptorFallback() {

    companion object {
        private const val MIN_VALID_LINES_SIZE = 1
        private val DELIMITERS = arrayOf(". ", "! ", "? ")
        private const val SYSTEM_DYNAMICALLY_PARAM = "book"
    }

    @CircuitBreaker(name = "aiPort", fallbackMethod = "callFallback")
    override suspend fun call(text: String, context: CoroutineContext): Collection<String> =
        withContext(context + Dispatchers.IO) {
            chatClient
                .prompt()
                .system { it.param(SYSTEM_DYNAMICALLY_PARAM, text) }
                .call()
                .content()
                .let { formatOutput(it) }
        }

    private suspend fun formatOutput(text: String): Collection<String> =
        text
            .replace("**", "")
            .lines()
            .takeIf { it.size > MIN_VALID_LINES_SIZE }
            ?: text.split(delimiters = DELIMITERS)
}
