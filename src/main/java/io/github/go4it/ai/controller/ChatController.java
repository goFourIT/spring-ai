package io.github.go4it.ai.controller;

import io.github.go4it.ai.dto.MessageDto;
import io.github.go4it.ai.service.ChatService;
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

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    MessageDto chat(@RequestBody final MessageDto messageDto) {
        LOGGER.info("Chat request came");

        return chatService.chat(messageDto);
    }
}
