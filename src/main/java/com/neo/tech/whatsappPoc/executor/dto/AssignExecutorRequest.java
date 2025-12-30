package com.neo.tech.whatsappPoc.executor.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssignExecutorRequest {
    private Long executorId;
    private BigDecimal assignedQuantity;
}

