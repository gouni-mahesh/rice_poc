package com.neo.tech.whatsappPoc.buyer.repository;

import com.neo.tech.whatsappPoc.buyer.entity.BuyerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BuyerRepository extends JpaRepository<BuyerEntity, Long> {

    boolean existsByBuyerCode(String buyerCode);

    Optional<BuyerEntity> findByBuyerCode(String buyerCode);
}
