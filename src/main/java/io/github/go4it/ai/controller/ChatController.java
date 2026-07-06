package io.github.go4it.ai.controller;

import io.github.go4it.ai.dto.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping
    Message chat(@RequestBody final Message message) {
        LOGGER.info("Chat request came");

        final String responseContent = chatClient.prompt()
                .user(message.content())
                .call()
                .content();

        return new Message(responseContent);
    }
}
