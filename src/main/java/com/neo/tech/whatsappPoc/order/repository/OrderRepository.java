package com.neo.tech.whatsappPoc.order.repository;

import com.neo.tech.whatsappPoc.order.entity.OrderEntity;
import com.neo.tech.whatsappPoc.util.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderCode(String orderCode);

    boolean existsByOrderCode(String orderCode);

    long countByStatus(OrderStatus status);
}
