package com.neo.tech.whatsappPoc.whatsapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WhatsAppMessageSender {

    @Value("${whatsapp.api.url}")
    private String apiUrl;

    @Value("${whatsapp.access-token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public void send(Map<String, Object> payload) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<Map<String, Object>> entity =
                new HttpEntity<>(payload, headers);

        try {
            restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            log.info("WhatsApp message sent");
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message", e);
        }
    }
}
