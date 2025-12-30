package com.neo.tech.whatsappPoc.supply.repository;

import com.neo.tech.whatsappPoc.supply.entity.OrderSupplyItemEntity;
import com.neo.tech.whatsappPoc.util.SupplyItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderSupplyItemRepository
        extends JpaRepository<OrderSupplyItemEntity, Long> {

    List<OrderSupplyItemEntity> findAllBySupplyPlan_Id(Long supplyPlanId);

    List<OrderSupplyItemEntity> findAllByStatus(SupplyItemStatus status);
}
