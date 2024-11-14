package com.almland.pdfebookconverter.infrastructure.adaptor.ai

import com.almland.pdfebookconverter.application.port.outbound.AIPort
import com.almland.pdfebookconverter.infrastructure.adaptor.ai.fallback.AIAdaptorFallback
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
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
    override fun call(text: String): Collection<String> =
        chatClient
            .prompt()
            .system { it.param(SYSTEM_DYNAMICALLY_PARAM, text) }
            .call()
            .content()
            .let { formatOutput(it) }

    private fun formatOutput(text: String): Collection<String> =
        text
            .replace("**", "")
            .lines()
            .takeIf { it.size > MIN_VALID_LINES_SIZE }
            ?: text.split(delimiters = DELIMITERS)
}
