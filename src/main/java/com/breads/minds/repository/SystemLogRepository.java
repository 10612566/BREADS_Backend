package com.breads.minds.repository;

import com.breads.minds.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {
    Page<SystemLog> findAllByOrderByTimestampDesc(Pageable pageable);
    Page<SystemLog> findByPerformedByOrderByTimestampDesc(String performedBy, Pageable pageable);
}
