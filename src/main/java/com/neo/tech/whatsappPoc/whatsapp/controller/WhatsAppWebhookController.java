package com.neo.tech.whatsappPoc.whatsapp.controller;


import com.neo.tech.whatsappPoc.user.service.UserService;
import com.neo.tech.whatsappPoc.whatsapp.dto.IncomingMessage;
import com.neo.tech.whatsappPoc.whatsapp.dto.WhatsAppWebhookPayload;
import com.neo.tech.whatsappPoc.whatsapp.service.WhatsAppMessageRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/webhook/whatsapp")
@RequiredArgsConstructor
@Slf4j
public class WhatsAppWebhookController {

    private final UserService userService;
    private final WhatsAppMessageRouter whatsAppMessageRouter;

    @Value("${whatsapp.verify-token}")
    private String verifyToken;

    // ---------------- VERIFY TOKEN ----------------
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam("hub.mode") String mode,
            @RequestParam("hub.verify_token") String token,
            @RequestParam("hub.challenge") String challenge
    ) {
        if ("subscribe".equals(mode) && verifyToken.equals(token)) {
            log.info("Webhook verified successfully");
            return ResponseEntity.ok(challenge);
        }
        return ResponseEntity.status(403).body("Verification failed");
    }
    @PostMapping
    public ResponseEntity<Void> receive(
            @RequestBody WhatsAppWebhookPayload payload
    ) {
        log.info("Received WhatsApp webhook payload");

        // 1️⃣ Ignore status-only callbacks
        if (payload.getEntry() == null
                || payload.getEntry().isEmpty()
                || payload.getEntry().getFirst()
                .getChanges().getFirst()
                .getValue().getMessages() == null) {
            return ResponseEntity.ok().build();
        }

        WhatsAppWebhookPayload.Message msg =
                payload.getEntry().getFirst()
                        .getChanges().getFirst()
                        .getValue().getMessages().getFirst();

        String mobile = msg.getFrom();
        IncomingMessage incoming;

        // 2️⃣ INTERACTIVE (LIST / BUTTON)
        if ("interactive".equals(msg.getType())) {

            if (msg.getInteractive().getList_reply() != null) {

                incoming = IncomingMessage.builder()
                        .mobile(mobile)
                        .selectionId(
                                msg.getInteractive()
                                        .getList_reply()
                                        .getId()
                        )
                        .type(IncomingMessage.MessageType.LIST)
                        .build();

            } else if (msg.getInteractive().getButton_reply() != null) {

                incoming = IncomingMessage.builder()
                        .mobile(mobile)
                        .selectionId(
                                msg.getInteractive()
                                        .getButton_reply()
                                        .getId()
                        )
                        .type(IncomingMessage.MessageType.BUTTON)
                        .build();

            } else {
                return ResponseEntity.ok().build();
            }

        }
        // 3️⃣ TEXT
        else {

            incoming = IncomingMessage.builder()
                    .mobile(mobile)
                    .text(msg.getText().getBody())
                    .type(IncomingMessage.MessageType.TEXT)
                    .build();
        }

        whatsAppMessageRouter.route(incoming);
        return ResponseEntity.ok().build();
    }


}


