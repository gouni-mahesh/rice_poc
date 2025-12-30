package com.neo.tech.whatsappPoc.common.audit;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity {

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

//    @Column(name = "created_by", updatable = false)
//    private String createdBy;
//
//    @Column(name = "updated_by")
//    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
//        this.createdBy = UserContext.getMobile();
//        this.updatedBy = UserContext.getMobile();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
//        this.updatedBy = UserContext.getMobile();
    }
}

