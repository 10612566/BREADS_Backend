package com.breads.minds.repository;

import com.breads.minds.entity.TrainingSession;
import com.breads.minds.entity.enums.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession> findByReportId(Long reportId);

    List<TrainingSession> findByReportIdAndTrainingType(Long reportId, TrainingType trainingType);

    void deleteByReportId(Long reportId);
}
