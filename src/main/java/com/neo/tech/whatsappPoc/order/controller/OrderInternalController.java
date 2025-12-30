package com.neo.tech.whatsappPoc.order.controller;

import com.neo.tech.whatsappPoc.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/internal/orders")
@RequiredArgsConstructor
public class OrderInternalController {

    private final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<String> createOrder(
            @RequestParam String managerMobile,
            @RequestParam Long buyerId,
            @RequestParam String riceType,
            @RequestParam BigDecimal quantity,
            @RequestParam String branchCode
    ) {
        orderService.createOrder(
                managerMobile,
                buyerId,
                riceType,
                quantity,
                branchCode
        );
        return ResponseEntity.ok("Order created successfully");
    }
}
