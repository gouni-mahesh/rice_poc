package com.neo.tech.whatsappPoc.executor.repository;

import com.neo.tech.whatsappPoc.executor.entity.ExecutorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ExecutorRepository extends JpaRepository<ExecutorEntity, Long> {

    @Query("""
    select distinct e.branchCode, e.branchName
    from ExecutorEntity e
""")
    List<Object[]> findDistinctBranches();


    Optional<ExecutorEntity> findByUserId(Long userId);

    List<ExecutorEntity> findAllByBranchCode(String branchCode);
    boolean existsByUserId(Long userId);

    Optional<ExecutorEntity> findByUser_MobileNumber(String mobileNumber);


}

