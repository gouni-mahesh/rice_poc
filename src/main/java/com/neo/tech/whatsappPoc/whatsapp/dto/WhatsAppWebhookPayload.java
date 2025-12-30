package com.neo.tech.whatsappPoc.whatsapp.dto;

import lombok.Data;
import java.util.List;

@Data
public class WhatsAppWebhookPayload {

    private List<Entry> entry;

    @Data
    public static class Entry {
        private List<Change> changes;
    }

    @Data
    public static class Change {
        private Value value;
    }

    @Data
    public static class Value {
        private List<Message> messages;
    }

    @Data
    public static class Message {
        private String from;
        private String type;              // text / interactive
        private Text text;
        private Interactive interactive;  // ✅ IMPORTANT
    }

    @Data
    public static class Text {
        private String body;
    }

    // ---------- INTERACTIVE ----------
    @Data
    public static class Interactive {
        private ListReply list_reply;
        private ButtonReply button_reply;
    }

    @Data
    public static class ListReply {
        private String id;     // SELECTED ROW ID
        private String title;
    }

    @Data
    public static class ButtonReply {
        private String id;
        private String title;
    }
}
