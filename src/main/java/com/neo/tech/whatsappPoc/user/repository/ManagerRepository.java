package com.neo.tech.whatsappPoc.user.repository;


import com.neo.tech.whatsappPoc.user.entity.ManagerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ManagerRepository extends JpaRepository<ManagerEntity, Long> {

    Optional<ManagerEntity> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
    Optional<ManagerEntity> findByUser_MobileNumber(String mobileNumber);

}

