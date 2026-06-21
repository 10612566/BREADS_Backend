package com.breads.minds.repository;

import com.breads.minds.entity.Notice;
import com.breads.minds.entity.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByIsActiveTrueOrderByCreatedAtDesc();

    @Query("""
        SELECT DISTINCT n FROM Notice n
        JOIN n.targetRoles r
        WHERE n.isActive = true
          AND r = :role
          AND (n.endDate IS NULL OR n.endDate >= :today)
        ORDER BY n.createdAt DESC
    """)
    List<Notice> findActiveNoticesForRole(@Param("role") UserRole role,
                                          @Param("today") LocalDate today);
}
