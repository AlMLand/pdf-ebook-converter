package com.almland.pdfebookconverter.infrastructure.configuration

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
internal class AIConfiguration {

    companion object {
        /**
         * @param {book} is dynamical, it's a placeholder for a book title
         */
        private const val SYSTEM_TEXT = "You are a friendly chatbot can you please recommend books like {book}, " +
                "your answer is maximum 100 words long"
    }

    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient =
        builder
            .defaultSystem(SYSTEM_TEXT)
            .build()
}
