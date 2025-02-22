package com.wxy.springbackend.controller;

import com.wxy.springbackend.service.ChatService;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class ChatController {
    private final ChatService chatService;
    ChatLanguageModel chatLanguageModel;
    public ChatController(ChatLanguageModel chatLanguageModel, ChatService chatService) {
        this.chatLanguageModel = chatLanguageModel;
        this.chatService = chatService;
    }
    @PostMapping("/chat")
    public Map<String, Object> model(@RequestParam String message) {
        return chatService.getResponse(message);
    }
}
