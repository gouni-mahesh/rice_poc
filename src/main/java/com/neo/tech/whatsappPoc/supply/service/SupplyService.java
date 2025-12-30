package com.neo.tech.whatsappPoc.supply.service;

import com.neo.tech.whatsappPoc.common.exception.ResourceNotFoundException;
import com.neo.tech.whatsappPoc.common.exception.UnauthorizedActionException;
import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import com.neo.tech.whatsappPoc.order.repository.OrderRepository;
import com.neo.tech.whatsappPoc.supply.entity.OrderSupplyItemEntity;
import com.neo.tech.whatsappPoc.supply.entity.OrderSupplyPlanEntity;
import com.neo.tech.whatsappPoc.supply.repository.OrderSupplyItemRepository;
import com.neo.tech.whatsappPoc.supply.repository.OrderSupplyPlanRepository;
import com.neo.tech.whatsappPoc.executor.entity.ExecutorEntity;
import com.neo.tech.whatsappPoc.executor.repository.ExecutorRepository;
import com.neo.tech.whatsappPoc.user.service.UserService;
import com.neo.tech.whatsappPoc.util.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyService {

    private final UserService userService;
    private final ExecutorRepository executorRepository;
    private final OrderRepository orderRepository;
    private final OrderSupplyPlanRepository supplyPlanRepository;
    private final OrderSupplyItemRepository supplyItemRepository;

    // ----------------------------------------------------
    // 1️⃣ CREATE SUPPLY PLAN (EXECUTOR ONLY)
    // ----------------------------------------------------

    @Transactional
    public void createSupplyPlan(
            String executorMobile,
            Long orderId,
            BigDecimal totalCommittedQuantity
    ) {

        // Authorization
        if (userService.getUserRole(executorMobile) != UserRole.EXECUTOR) {
            throw new UnauthorizedActionException("Only executor can create supply plan");
        }

        if (totalCommittedQuantity == null
                || totalCommittedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Committed quantity must be positive");
        }

        ExecutorEntity executor = executorRepository
                .findByUser_MobileNumber(executorMobile)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Executor not found")
                );

        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found")
                );

        // Ensure only one plan per executor per order
        if (supplyPlanRepository
                .findByOrder_IdAndExecutor_Id(orderId, executor.getId())
                .isPresent()) {
            throw new ValidationException("Supply plan already exists for this order");
        }

        OrderSupplyPlanEntity plan = OrderSupplyPlanEntity.builder()
                .order(order)
                .executor(executor)
                .totalCommittedQuantity(totalCommittedQuantity)
                .status(SupplyPlanStatus.DRAFT)
                .build();

        supplyPlanRepository.save(plan);

        log.info("Supply plan created: orderId={}, executor={}",
                orderId, executorMobile);
    }

    // ----------------------------------------------------
    // 2️⃣ ADD MILLER SUPPLY ITEM
    // ----------------------------------------------------

    @Transactional
    public void addSupplyItem(
            String executorMobile,
            Long supplyPlanId,
            String millerName,
            MillerType millerType,
            BigDecimal plannedQuantity,
            LocalDate expectedDispatchDate
    ) {

        authorizeExecutor(executorMobile);

        if (plannedQuantity == null
                || plannedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Planned quantity must be positive");
        }

        OrderSupplyPlanEntity plan = supplyPlanRepository.findById(supplyPlanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Supply plan not found")
                );

        validatePlanOwnership(executorMobile, plan);

        OrderSupplyItemEntity item = OrderSupplyItemEntity.builder()
                .supplyPlan(plan)
                .millerName(millerName.trim())
                .millerType(millerType)
                .plannedQuantity(plannedQuantity)
                .dispatchedQuantity(BigDecimal.ZERO)
                .expectedDispatchDate(expectedDispatchDate)
                .status(SupplyItemStatus.PLANNED)
                .build();

        supplyItemRepository.save(item);

        log.info("Supply item added: planId={}, miller={}",
                supplyPlanId, millerName);
    }

    // ----------------------------------------------------
    // 3️⃣ SUBMIT SUPPLY PLAN
    // ----------------------------------------------------

    @Transactional
    public void submitSupplyPlan(String executorMobile, Long supplyPlanId) {

        authorizeExecutor(executorMobile);

        OrderSupplyPlanEntity plan = supplyPlanRepository.findById(supplyPlanId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Supply plan not found")
                );

        validatePlanOwnership(executorMobile, plan);

        List<OrderSupplyItemEntity> items =
                supplyItemRepository.findAllBySupplyPlan_Id(plan.getId());

        BigDecimal sum = items.stream()
                .map(OrderSupplyItemEntity::getPlannedQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (sum.compareTo(plan.getTotalCommittedQuantity()) != 0) {
            throw new ValidationException(
                    "Sum of miller quantities must match committed quantity"
            );
        }

        plan.setStatus(SupplyPlanStatus.SUBMITTED);
        supplyPlanRepository.save(plan);

        updateOrderPlannedQuantity(plan.getOrder());

        log.info("Supply plan submitted: planId={}", supplyPlanId);
    }

    // ----------------------------------------------------
    // 4️⃣ DISPATCH UPDATE
    // ----------------------------------------------------

    @Transactional
    public void updateDispatch(
            String executorMobile,
            Long supplyItemId,
            BigDecimal dispatchedQuantity,
            LocalDate actualDispatchDate
    ) {

        authorizeExecutor(executorMobile);

        if (dispatchedQuantity == null
                || dispatchedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Dispatched quantity must be positive");
        }

        OrderSupplyItemEntity item = supplyItemRepository.findById(supplyItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Supply item not found")
                );

        validatePlanOwnership(executorMobile, item.getSupplyPlan());

        item.setDispatchedQuantity(dispatchedQuantity);
        item.setActualDispatchDate(actualDispatchDate);
        item.setStatus(SupplyItemStatus.DISPATCHED);

        supplyItemRepository.save(item);

        updateOrderDispatchedQuantity(item.getSupplyPlan().getOrder());

        log.info("Dispatch updated: itemId={}, qty={}",
                supplyItemId, dispatchedQuantity);
    }

    // ----------------------------------------------------
    // INTERNAL HELPERS
    // ----------------------------------------------------

    private void authorizeExecutor(String executorMobile) {
        if (userService.getUserRole(executorMobile) != UserRole.EXECUTOR) {
            throw new UnauthorizedActionException("Only executor allowed");
        }
    }

    private void validatePlanOwnership(
            String executorMobile,
            OrderSupplyPlanEntity plan
    ) {
        String planExecutorMobile =
                plan.getExecutor().getUser().getMobileNumber();

        if (!executorMobile.equals(planExecutorMobile)) {
            throw new UnauthorizedActionException(
                    "Executor does not own this supply plan"
            );
        }
    }

    private void updateOrderPlannedQuantity(OrderEntity order) {

        BigDecimal planned = supplyPlanRepository
                .findAllByOrder_Id(order.getId())
                .stream()
                .map(OrderSupplyPlanEntity::getTotalCommittedQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setPlannedQuantity(planned);

        if (planned.compareTo(order.getTotalQuantity()) >= 0) {
            order.setStatus(OrderStatus.FULLY_PLANNED);
        } else {
            order.setStatus(OrderStatus.PARTIALLY_PLANNED);
        }

        orderRepository.save(order);
    }

    private void updateOrderDispatchedQuantity(OrderEntity order) {

        BigDecimal dispatched = supplyItemRepository
                .findAllByStatus(SupplyItemStatus.DISPATCHED)
                .stream()
                .filter(i ->
                        i.getSupplyPlan().getOrder().getId().equals(order.getId())
                )
                .map(OrderSupplyItemEntity::getDispatchedQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setDispatchedQuantity(dispatched);

        if (dispatched.compareTo(order.getTotalQuantity()) >= 0) {
            order.setStatus(OrderStatus.COMPLETED);
        } else {
            order.setStatus(OrderStatus.PARTIALLY_DISPATCHED);
        }

        orderRepository.save(order);
    }
}
