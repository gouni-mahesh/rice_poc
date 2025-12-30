package com.neo.tech.whatsappPoc.whatsapp.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class IncomingMessage {

    private final String mobile;

    // for TEXT messages
    private final String text;

    // for LIST / BUTTON selections
    private final String selectionId;

    private final MessageType type;

    public enum MessageType {
        TEXT,
        LIST,
        BUTTON
    }
}
