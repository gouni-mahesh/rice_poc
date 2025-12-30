package com.neo.tech.whatsappPoc.whatsapp.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WhatsAppReplyService {

    private final WhatsAppMessageSender sender;

    // ---------------- TEXT ----------------

    public void sendText(String to, String text) {

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "text",
                "text", Map.of("body", text)
        );

        sender.send(payload);
    }

    // ---------------- LIST ----------------

    public void sendList(
            String to,
            String header,
            String body,
            String button,
            List<Map<String, String>> rows
    ) {

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "list",
                        "header", Map.of(
                                "type", "text",
                                "text", header
                        ),
                        "body", Map.of(
                                "text", body
                        ),
                        "action", Map.of(
                                "button", button,
                                "sections", List.of(
                                        Map.of(
                                                "title", "Options",
                                                "rows", rows
                                        )
                                )
                        )
                )
        );

        sender.send(payload);
    }
    public void sendButtons(
            String to,
            String body,
            List<Map<String, Object>> buttons
    ) {

        Map<String, Object> payload = Map.of(
                "messaging_product", "whatsapp",
                "to", to,
                "type", "interactive",
                "interactive", Map.of(
                        "type", "button",
                        "body", Map.of(
                                "text", body
                        ),
                        "action", Map.of(
                                "buttons", buttons
                        )
                )
        );

        sender.send(payload);
    }

}
