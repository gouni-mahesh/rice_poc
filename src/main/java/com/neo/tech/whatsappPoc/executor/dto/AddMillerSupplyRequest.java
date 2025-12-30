package com.neo.tech.whatsappPoc.executor.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data

public class AddMillerSupplyRequest {
    private String millerName;
    private BigDecimal suppliedQuantity;
    private String remarks;
}

