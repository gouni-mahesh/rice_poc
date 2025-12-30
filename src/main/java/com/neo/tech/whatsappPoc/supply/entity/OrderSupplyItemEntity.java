package com.neo.tech.whatsappPoc.supply.entity;

import com.neo.tech.whatsappPoc.common.audit.AuditableEntity;
import com.neo.tech.whatsappPoc.util.MillerType;
import com.neo.tech.whatsappPoc.util.SupplyItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "order_supply_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSupplyItemEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------- RELATIONSHIP ----------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supply_plan_id", nullable = false)
    private OrderSupplyPlanEntity supplyPlan;

    // ---------------- MILLER INFO ----------------

    @Column(name = "miller_name", nullable = false, length = 150)
    private String millerName;

    @Enumerated(EnumType.STRING)
    @Column(name = "miller_type", nullable = false, length = 20)
    private MillerType millerType;

    // ---------------- QUANTITIES ----------------

    @Column(name = "planned_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal plannedQuantity;

    @Column(name = "dispatched_quantity", precision = 12, scale = 3)
    private BigDecimal dispatchedQuantity;

    // ---------------- DATES ----------------

    @Column(name = "expected_dispatch_date")
    private LocalDate expectedDispatchDate;

    @Column(name = "actual_dispatch_date")
    private LocalDate actualDispatchDate;

    // ---------------- STATUS ----------------

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupplyItemStatus status;
}
