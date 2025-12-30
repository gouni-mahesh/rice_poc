package com.neo.tech.whatsappPoc.buyer.service;

import com.neo.tech.whatsappPoc.buyer.dto.BuyerRequestDTO;
import com.neo.tech.whatsappPoc.buyer.entity.BuyerEntity;
import com.neo.tech.whatsappPoc.buyer.repository.BuyerRepository;
import com.neo.tech.whatsappPoc.common.exception.UnauthorizedActionException;
import com.neo.tech.whatsappPoc.user.service.UserService;
import com.neo.tech.whatsappPoc.util.UserRole;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final UserService userService;
    public String saveBuyer(BuyerRequestDTO dto) {

        if (buyerRepository.existsByBuyerCode(dto.getBuyerCode())) {
            throw new RuntimeException("Buyer code already exists");
        }

        BuyerEntity buyer = BuyerEntity.builder()
                .buyerName(dto.getBuyerName())
                .buyerCode(dto.getBuyerCode())
                .mobileNumber(dto.getMobileNumber())
                .build();

        buyerRepository.save(buyer);

        return "Buyer saved successfully";
    }
    /**
     * COMMAND METHOD
     * Creates buyer (Manager only)
     */
    @Transactional
    public void createBuyer(
            String managerMobile,
            String buyerName,
            String buyerCode,
            String buyerMobile
    ) {

        // 1️⃣ Authorization (Manager only)
        UserRole role = userService.getUserRole(managerMobile);
        if (role != UserRole.MANAGER) {
            log.warn("Unauthorized buyer creation attempt by mobile={}", managerMobile);
            throw new UnauthorizedActionException("Only manager can create buyer");
        }

        // 2️⃣ Validation
        validateBuyerInput(buyerName, buyerCode);

        if (buyerRepository.existsByBuyerCode(buyerCode)) {
            throw new ValidationException("Buyer code already exists");
        }

        // 3️⃣ Save buyer
        BuyerEntity buyer = BuyerEntity.builder()
                .buyerName(buyerName.trim())
                .buyerCode(buyerCode.trim())
                .mobileNumber(buyerMobile)
                .build();

        buyerRepository.save(buyer);

        log.info("Buyer created successfully: code={}, by={}", buyerCode, managerMobile);
    }

    // ----------------- PRIVATE VALIDATIONS -----------------

    private void validateBuyerInput(String buyerName, String buyerCode) {

        if (buyerName == null || buyerName.isBlank()) {
            throw new ValidationException("Buyer name is required");
        }

        if (buyerCode == null || buyerCode.isBlank()) {
            throw new ValidationException("Buyer code is required");
        }
    }
}
