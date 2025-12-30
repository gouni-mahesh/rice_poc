package com.neo.tech.whatsappPoc.order.service;

import com.neo.tech.whatsappPoc.buyer.entity.BuyerEntity;
import com.neo.tech.whatsappPoc.buyer.repository.BuyerRepository;
import com.neo.tech.whatsappPoc.common.exception.ResourceNotFoundException;
import com.neo.tech.whatsappPoc.common.exception.UnauthorizedActionException;
import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import com.neo.tech.whatsappPoc.order.repository.OrderRepository;
import com.neo.tech.whatsappPoc.user.entity.ManagerEntity;
import com.neo.tech.whatsappPoc.user.repository.ManagerRepository;
import com.neo.tech.whatsappPoc.user.service.UserService;
import com.neo.tech.whatsappPoc.util.OrderStatus;
import com.neo.tech.whatsappPoc.util.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final BuyerRepository buyerRepository;
    private final ManagerRepository managerRepository;
    private final UserService userService;

    /**
     * COMMAND METHOD
     * Creates order (Manager only)
     */
    @Transactional
    public OrderEntity createOrder(
            String managerMobile,
            Long buyerId,
            String riceType,
            BigDecimal totalQuantity,
            String branchCode
    ) {

        // 1️⃣ Authorization (Manager only)
        UserRole role = userService.getUserRole(managerMobile);
        if (role != UserRole.MANAGER) {
            log.warn("Unauthorized order creation attempt by mobile={}", managerMobile);
            throw new UnauthorizedActionException("Only manager can create order");
        }

        // 2️⃣ Validate inputs
//        validateOrderInput(buyerId, riceType, totalQuantity, branchCode);

        // 3️⃣ Fetch buyer
        BuyerEntity buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Buyer not found")
                );

        // 4️⃣ Fetch manager
        ManagerEntity manager = managerRepository.findByUser_MobileNumber(managerMobile)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Manager not found")
                );

        // 5️⃣ Create order
        OrderEntity order = OrderEntity.builder()
                .orderCode(generateOrderCode())
                .buyer(buyer)
                .createdByManager(manager)
                .riceType(riceType.trim())
                .branchCode(branchCode.trim())
                .totalQuantity(totalQuantity)
                .plannedQuantity(BigDecimal.ZERO)
                .dispatchedQuantity(BigDecimal.ZERO)
                .status(OrderStatus.CREATED)
                .build();

        orderRepository.save(order);

        log.info(
                "Order created: code={}, buyerId={}, qty={}, by={}",
                order.getOrderCode(),
                buyerId,
                totalQuantity,
                managerMobile
        );
        return order;
    }

    // ---------------- PRIVATE HELPERS ----------------

//    private void validateOrderInput(
//            Long buyerId,
//            String riceType,
//            BigDecimal totalQuantity,
//            String branchCode
//    ) {
//
//        if (buyerId == null) {
//            throw new ValidationException("Buyer is required");
//        }
//
//        if (riceType == null || riceType.isBlank()) {
//            throw new ValidationException("Rice type is required");
//        }
//
//        if (branchCode == null || branchCode.isBlank()) {
//            throw new ValidationException("Branch code is required");
//        }
//
//        if (totalQuantity == null || totalQuantity.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new ValidationException("Total quantity must be greater than zero");
//        }
//    }

    private String generateOrderCode() {
        return "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
