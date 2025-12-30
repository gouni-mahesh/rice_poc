package com.neo.tech.whatsappPoc.util;

import com.neo.tech.whatsappPoc.whatsapp.service.WhatsAppServiceTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ContactEventListener {

    private final WhatsAppServiceTemplate whatsAppService;
    @Async
    @EventListener
    public void handleContactCreatedEventWhatsApp(ContactCreatedEvent event) {
        log.info("Received orderId: {}", event.getOrderEntity().getId());
        whatsAppService.sendFirstPendingContactMessage("hello_world",event.getBranchIds() , event.getOrderEntity());

    }
}
