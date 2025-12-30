package com.neo.tech.whatsappPoc.order.dto;


import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponse {

    private Long orderId;
    private String productType;
    private BigDecimal quantity;
    private String status;
    private Long managerId;
}

