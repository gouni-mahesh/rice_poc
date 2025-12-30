package com.neo.tech.whatsappPoc.whatsapp.repository;

import com.neo.tech.whatsappPoc.whatsapp.entity.WhatsAppConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface WhatsAppConversationRepository
        extends JpaRepository<WhatsAppConversationEntity, Long> {

    Optional<WhatsAppConversationEntity> findByMobileNumber(String mobileNumber);

    void deleteByMobileNumber(String mobileNumber);
}
