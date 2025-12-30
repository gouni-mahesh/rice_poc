package com.neo.tech.whatsappPoc.whatsapp.entity;


import com.neo.tech.whatsappPoc.common.audit.AuditableEntity;
import com.neo.tech.whatsappPoc.util.ConversationFlow;
import com.neo.tech.whatsappPoc.util.ConversationStep;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "whatsapp_conversation",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "mobile_number")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppConversationEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow", nullable = false, length = 50)
    private ConversationFlow flow;

    @Enumerated(EnumType.STRING)
    @Column(name = "step", nullable = false, length = 50)
    private ConversationStep step;

    /**
     * Stores temporary values:
     * buyerId, riceType, quantity, branchCode etc.
     */
    @Lob
    @Column(name = "context_json")
    private String contextJson;
}
