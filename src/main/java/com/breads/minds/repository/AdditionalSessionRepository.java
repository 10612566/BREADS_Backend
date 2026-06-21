package com.breads.minds.repository;

import com.breads.minds.entity.AdditionalSession;
import com.breads.minds.entity.enums.AdditionalSessionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdditionalSessionRepository extends JpaRepository<AdditionalSession, Long> {
    List<AdditionalSession> findByReportId(Long reportId);
    List<AdditionalSession> findByReportIdAndSessionType(Long reportId, AdditionalSessionType sessionType);
    void deleteByReportId(Long reportId);
}
