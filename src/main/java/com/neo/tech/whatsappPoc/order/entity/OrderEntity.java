package com.neo.tech.whatsappPoc.order.entity;


import com.neo.tech.whatsappPoc.buyer.entity.BuyerEntity;
import com.neo.tech.whatsappPoc.common.audit.AuditableEntity;
import com.neo.tech.whatsappPoc.user.entity.ManagerEntity;
import com.neo.tech.whatsappPoc.util.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Business order identifier (human readable)
    @Column(name = "order_code", nullable = false, unique = true, length = 50)
    private String orderCode;

    // ---------------- RELATIONSHIPS ----------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "buyer_id", nullable = false)
    private BuyerEntity buyer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "manager_id", nullable = false)
    private ManagerEntity createdByManager;

    // ---------------- ORDER DETAILS ----------------

    @Column(name = "rice_type", nullable = false, length = 100)
    private String riceType;

    @Column(name = "branch_code", nullable = false, length = 50)
    private String branchCode;

    // ---------------- QUANTITIES ----------------

    @Column(name = "total_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal totalQuantity;

    @Column(name = "planned_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal plannedQuantity;

    @Column(name = "dispatched_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal dispatchedQuantity;

    // ---------------- STATUS ----------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;
}

