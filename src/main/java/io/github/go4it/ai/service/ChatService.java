package io.github.go4it.ai.service;

import io.github.go4it.ai.dto.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public MessageDto chat(final MessageDto messageDto) {
        final String conversationId = messageDto.conversationId() != null ?
                messageDto.conversationId() : UUID.randomUUID().toString();

        LOGGER.debug("Conversation id {} has been assigned", conversationId);

        final String responseContent = chatClient.prompt()
                .user(messageDto.content())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

        return new MessageDto(conversationId, responseContent);
    }
}
