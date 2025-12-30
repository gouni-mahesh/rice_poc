package com.neo.tech.whatsappPoc.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public <T> void publishEvent(T event, NotificationEvent eventType) {
        log.info("Entering EventPublisher: publishEvent event={}", eventType);
        try {
            log.info("EventPublisher: SafePublishing event={}", eventType);
            applicationEventPublisher.publishEvent(event);
            log.info("Exiting EventPublisher: publishEvent event={}", eventType);
        } catch (Exception ex) {
            log.error("EventPublisher: Error occurred while publishing event={}", eventType, ex);
        }
    }
}
