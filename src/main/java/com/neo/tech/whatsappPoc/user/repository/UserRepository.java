package com.neo.tech.whatsappPoc.user.repository;


import com.neo.tech.whatsappPoc.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByMobileNumber(String mobileNumber);

    boolean existsByMobileNumber(String mobileNumber);
//    boolean existsByUserId(Long id);

}

