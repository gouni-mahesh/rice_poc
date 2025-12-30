package com.neo.tech.whatsappPoc.supply.entity;

import com.neo.tech.whatsappPoc.common.audit.AuditableEntity;
import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import com.neo.tech.whatsappPoc.executor.entity.ExecutorEntity;
import com.neo.tech.whatsappPoc.util.SupplyPlanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_supply_plan",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"order_id", "executor_id"})
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSupplyPlanEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------- RELATIONSHIPS ----------------

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "executor_id", nullable = false)
    private ExecutorEntity executor;

    // ---------------- SUPPLY INFO ----------------

    @Column(name = "total_committed_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal totalCommittedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SupplyPlanStatus status;
}
