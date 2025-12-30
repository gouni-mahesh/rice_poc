package com.neo.tech.whatsappPoc.order.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateOrderRequest {

    @NotNull
    private Long managerId;

    @NotBlank
    private String productType;

    @NotNull
    private BigDecimal quantity;
}

