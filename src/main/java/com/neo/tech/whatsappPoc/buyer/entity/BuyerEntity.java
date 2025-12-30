package com.neo.tech.whatsappPoc.buyer.entity;


import com.neo.tech.whatsappPoc.common.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "buyer",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "buyer_code")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "buyer_name", nullable = false, length = 150)
    private String buyerName;

    @Column(name = "buyer_code", nullable = false, length = 50)
    private String buyerCode;

    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;
}

