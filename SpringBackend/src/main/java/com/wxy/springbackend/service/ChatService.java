package com.wxy.springbackend.service;

import com.wxy.springbackend.repository.ChatRepository;
import dev.ai4j.openai4j.chat.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.springframework.stereotype.Service;
import dev.langchain4j.data.message.ChatMessage;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    ChatRepository chatRepository;
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public Map<String, Object> getResponse(String message) {
        return chatRepository.getResponse(message);
    }
}
