package com.neo.tech.whatsappPoc.supply.repository;

import com.neo.tech.whatsappPoc.supply.entity.OrderSupplyPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface OrderSupplyPlanRepository
        extends JpaRepository<OrderSupplyPlanEntity, Long> {

    Optional<OrderSupplyPlanEntity> findByOrder_IdAndExecutor_Id(
            Long orderId,
            Long executorId
    );

    List<OrderSupplyPlanEntity> findAllByOrder_Id(Long orderId);
}
