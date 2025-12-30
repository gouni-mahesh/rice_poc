package com.neo.tech.whatsappPoc.supply.controller;

import com.neo.tech.whatsappPoc.supply.service.SupplyService;
import com.neo.tech.whatsappPoc.util.MillerType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/internal/supply")
@RequiredArgsConstructor
public class SupplyInternalController {

    private final SupplyService supplyService;

    @PostMapping("/plan")
    public ResponseEntity<String> createPlan(
            @RequestParam String executorMobile,
            @RequestParam Long orderId,
            @RequestParam BigDecimal quantity
    ) {
        supplyService.createSupplyPlan(executorMobile, orderId, quantity);
        return ResponseEntity.ok("Supply plan created");
    }

    @PostMapping("/item")
    public ResponseEntity<String> addItem(
            @RequestParam String executorMobile,
            @RequestParam Long planId,
            @RequestParam String millerName,
            @RequestParam MillerType millerType,
            @RequestParam BigDecimal quantity,
            @RequestParam LocalDate expectedDate
    ) {
        supplyService.addSupplyItem(
                executorMobile,
                planId,
                millerName,
                millerType,
                quantity,
                expectedDate
        );
        return ResponseEntity.ok("Supply item added");
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitPlan(
            @RequestParam String executorMobile,
            @RequestParam Long planId
    ) {
        supplyService.submitSupplyPlan(executorMobile, planId);
        return ResponseEntity.ok("Supply plan submitted");
    }

    @PostMapping("/dispatch")
    public ResponseEntity<String> dispatch(
            @RequestParam String executorMobile,
            @RequestParam Long itemId,
            @RequestParam BigDecimal quantity,
            @RequestParam LocalDate dispatchDate
    ) {
        supplyService.updateDispatch(
                executorMobile,
                itemId,
                quantity,
                dispatchDate
        );
        return ResponseEntity.ok("Dispatch updated");
    }
}
