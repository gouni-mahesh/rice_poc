package com.neo.tech.whatsappPoc.whatsapp.service;


import com.neo.tech.whatsappPoc.util.ConversationFlow;
import com.neo.tech.whatsappPoc.util.ConversationStep;
import com.neo.tech.whatsappPoc.whatsapp.entity.WhatsAppConversationEntity;
import com.neo.tech.whatsappPoc.whatsapp.repository.WhatsAppConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ConversationStateService {

    private final WhatsAppConversationRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WhatsAppConversationEntity startFlow(
            String mobile,
            ConversationFlow flow,
            ConversationStep step
    ) {
        repository.deleteByMobileNumber(mobile);

        WhatsAppConversationEntity conversation =
                WhatsAppConversationEntity.builder()
                        .mobileNumber(mobile)
                        .flow(flow)
                        .step(step)
                        .contextJson("{}")
                        .build();

        return repository.save(conversation);
    }

    public WhatsAppConversationEntity get(String mobile) {
        return repository.findByMobileNumber(mobile).orElse(null);
    }
    public void reset(WhatsAppConversationEntity conversation) {
        conversation.setFlow(ConversationFlow.NONE);
        conversation.setStep(ConversationStep.START);
        conversation.setContextJson(null);
        repository.save(conversation);
    }

    public void updateStep(
            WhatsAppConversationEntity conversation,
            ConversationStep step
    ) {
        conversation.setStep(step);
        repository.save(conversation);
    }

    public void putContext(
            WhatsAppConversationEntity conversation,
            String key,
            Object value
    ) {
        Map<String, Object> map = readContext(conversation);
        map.put(key, value);
        writeContext(conversation, map);
    }

    public Object getContext(
            WhatsAppConversationEntity conversation,
            String key
    ) {
        return readContext(conversation).get(key);
    }

    public void clear(String mobile) {
        repository.deleteByMobileNumber(mobile);
    }

    // ---------- INTERNAL JSON HELPERS ----------

    private Map<String, Object> readContext(
            WhatsAppConversationEntity conversation
    ) {
        try {
            return objectMapper.readValue(
                    conversation.getContextJson(),
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void writeContext(
            WhatsAppConversationEntity conversation,
            Map<String, Object> map
    ) {
        try {
            conversation.setContextJson(
                    objectMapper.writeValueAsString(map)
            );
            repository.save(conversation);
        } catch (Exception ignored) {}
    }
    public void updateFlowAndStep(
            WhatsAppConversationEntity conversation,
            ConversationFlow flow,
            ConversationStep step
    ) {
        conversation.setFlow(flow);
        conversation.setStep(step);
        repository.save(conversation);
    }
}

