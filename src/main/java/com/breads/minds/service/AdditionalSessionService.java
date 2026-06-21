package com.breads.minds.service;

import com.breads.minds.dto.request.AdditionalSessionRequest;
import com.breads.minds.entity.AdditionalSession;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.enums.AdditionalSessionType;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.AdditionalSessionRepository;
import com.breads.minds.repository.BeneficiaryReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdditionalSessionService {

    private final AdditionalSessionRepository sessionRepository;
    private final BeneficiaryReportRepository reportRepository;

    public List<AdditionalSession> getByReport(Long reportId) {
        return sessionRepository.findByReportId(reportId);
    }

    public List<AdditionalSession> getByReportAndType(Long reportId, AdditionalSessionType type) {
        return sessionRepository.findByReportIdAndSessionType(reportId, type);
    }

    public AdditionalSession getById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Additional session not found: " + id));
    }

    @Transactional
    public AdditionalSession add(AdditionalSessionRequest req) {
        BeneficiaryReport report = reportRepository.findById(req.getReportId())
                .orElseThrow(() -> new ResourceNotFoundException("Report not found: " + req.getReportId()));

        if (Boolean.TRUE.equals(report.getIsLocked())) {
            throw new IllegalStateException("Report is locked and cannot be modified");
        }

        AdditionalSession session = AdditionalSession.builder()
                .report(report)
                .sessionType(req.getSessionType())
                .slNo(req.getSlNo())
                .date(req.getDate())
                .place(req.getPlace())
                .schoolName(req.getSchoolName())
                .participantsMale(req.getParticipantsMale() != null ? req.getParticipantsMale() : 0)
                .participantsFemale(req.getParticipantsFemale() != null ? req.getParticipantsFemale() : 0)
                .participantsTotal(req.getParticipantsTotal() != null ? req.getParticipantsTotal() : 0)
                .remarks(req.getRemarks())
                .build();

        return sessionRepository.save(session);
    }

    @Transactional
    public AdditionalSession update(Long id, AdditionalSessionRequest req) {
        AdditionalSession session = getById(id);

        if (Boolean.TRUE.equals(session.getReport().getIsLocked())) {
            throw new IllegalStateException("Report is locked and cannot be modified");
        }

        session.setSessionType(req.getSessionType());
        session.setSlNo(req.getSlNo());
        session.setDate(req.getDate());
        session.setPlace(req.getPlace());
        session.setSchoolName(req.getSchoolName());
        session.setParticipantsMale(req.getParticipantsMale() != null ? req.getParticipantsMale() : 0);
        session.setParticipantsFemale(req.getParticipantsFemale() != null ? req.getParticipantsFemale() : 0);
        session.setParticipantsTotal(req.getParticipantsTotal() != null ? req.getParticipantsTotal() : 0);
        session.setRemarks(req.getRemarks());

        return sessionRepository.save(session);
    }

    @Transactional
    public void delete(Long id) {
        AdditionalSession session = getById(id);
        if (Boolean.TRUE.equals(session.getReport().getIsLocked())) {
            throw new IllegalStateException("Report is locked and cannot be modified");
        }
        sessionRepository.delete(session);
    }
}
