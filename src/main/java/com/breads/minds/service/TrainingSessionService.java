package com.breads.minds.service;

import com.breads.minds.dto.request.TrainingSessionRequest;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.TrainingSession;
import com.breads.minds.entity.enums.TrainingType;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.BeneficiaryReportRepository;
import com.breads.minds.repository.TrainingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingSessionService {

    private final TrainingSessionRepository sessionRepository;
    private final BeneficiaryReportRepository reportRepository;
    private final SystemLogService systemLogService;

    public List<TrainingSession> getSessionsByReport(Long reportId) {
        return sessionRepository.findByReportId(reportId);
    }

    public List<TrainingSession> getSessionsByReportAndType(Long reportId, TrainingType trainingType) {
        return sessionRepository.findByReportIdAndTrainingType(reportId, trainingType);
    }

    public TrainingSession getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Training session not found: " + id));
    }

    @Transactional
    public TrainingSession addSession(TrainingSessionRequest req, String username) {
        BeneficiaryReport report = reportRepository.findById(req.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + req.getReportId()));

        if (Boolean.TRUE.equals(report.getIsLocked())) {
            throw new IllegalStateException("Report is locked; cannot add training sessions");
        }

        TrainingSession session = TrainingSession.builder()
                .report(report)
                .trainingType(req.getTrainingType())
                .slNo(req.getSlNo())
                .date(req.getDate())
                .place(req.getPlace())
                .schoolName(req.getSchoolName())
                .participantsMale(req.getParticipantsMale() != null ? req.getParticipantsMale() : 0)
                .participantsFemale(req.getParticipantsFemale() != null ? req.getParticipantsFemale() : 0)
                .participantsTotal(req.getParticipantsTotal() != null ? req.getParticipantsTotal() : 0)
                .build();

        TrainingSession saved = sessionRepository.save(session);
        systemLogService.log(username, "ADD_TRAINING_SESSION",
                req.getTrainingType() + " session added to report ID " + req.getReportId());
        return saved;
    }

    @Transactional
    public TrainingSession updateSession(Long id, TrainingSessionRequest req, String username) {
        TrainingSession session = getSessionById(id);

        if (Boolean.TRUE.equals(session.getReport().getIsLocked())) {
            throw new IllegalStateException("Report is locked; cannot modify training sessions");
        }

        session.setTrainingType(req.getTrainingType());
        session.setSlNo(req.getSlNo());
        session.setDate(req.getDate());
        session.setPlace(req.getPlace());
        session.setSchoolName(req.getSchoolName());
        session.setParticipantsMale(req.getParticipantsMale() != null ? req.getParticipantsMale() : 0);
        session.setParticipantsFemale(req.getParticipantsFemale() != null ? req.getParticipantsFemale() : 0);
        session.setParticipantsTotal(req.getParticipantsTotal() != null ? req.getParticipantsTotal() : 0);

        systemLogService.log(username, "UPDATE_TRAINING_SESSION", "Training session updated: ID " + id);
        return sessionRepository.save(session);
    }

    @Transactional
    public void deleteSession(Long id, String username) {
        TrainingSession session = getSessionById(id);

        if (Boolean.TRUE.equals(session.getReport().getIsLocked())) {
            throw new IllegalStateException("Report is locked; cannot delete training sessions");
        }

        sessionRepository.deleteById(id);
        systemLogService.log(username, "DELETE_TRAINING_SESSION", "Training session deleted: ID " + id);
    }
}
