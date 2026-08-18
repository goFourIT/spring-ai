package io.github.go4it.ai.configuration;

import io.github.go4it.ai.advisor.LogAdvisor;
import io.github.go4it.ai.tool.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean
    @Qualifier("inMemoryMessageWindowChatMemory")
    public ChatMemory messageWindowChatMemory() {
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    @Qualifier("persistedMessageWindowChatMemory")
    public ChatMemory persistedMessageWindowChatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(
            @Qualifier("persistedMessageWindowChatMemory") ChatMemory chatMemory
    ) {
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
    }

    @Bean
    public ChatClient chatClient(
            ChatClient.Builder chatClientBuilder,
            MessageChatMemoryAdvisor messageChatMemoryAdvisor,
            LogAdvisor logAdvisor,
            DateTimeTools dateTimeTools
    ) {
        return chatClientBuilder.defaultSystem("You're a helpful assistant. Use tools only if you think are useful")
                .defaultAdvisors(logAdvisor, messageChatMemoryAdvisor)
                .defaultTools(dateTimeTools)
                .build();
    }
}
