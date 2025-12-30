package com.neo.tech.whatsappPoc.buyer.controller;

import com.neo.tech.whatsappPoc.buyer.dto.BuyerRequestDTO;
import com.neo.tech.whatsappPoc.buyer.service.BuyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/buyers")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @PostMapping("/create")
    public String createBuyer(@RequestBody BuyerRequestDTO request) {
        return buyerService.saveBuyer(request);
    }
}
