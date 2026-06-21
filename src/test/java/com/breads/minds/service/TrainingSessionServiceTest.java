package com.breads.minds.service;

import com.breads.minds.dto.request.TrainingSessionRequest;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.TrainingSession;
import com.breads.minds.entity.enums.TrainingType;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.BeneficiaryReportRepository;
import com.breads.minds.repository.TrainingSessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingSessionServiceTest {

    @Mock private TrainingSessionRepository sessionRepository;
    @Mock private BeneficiaryReportRepository reportRepository;
    @Mock private SystemLogService systemLogService;
    @InjectMocks private TrainingSessionService trainingSessionService;

    private BeneficiaryReport report(Boolean locked) {
        return BeneficiaryReport.builder().id(1L).isLocked(locked).build();
    }

    private TrainingSession session(Boolean reportLocked) {
        return TrainingSession.builder().id(1L).report(report(reportLocked))
                .trainingType(TrainingType.TEACHER_TRAINING).build();
    }

    private TrainingSessionRequest request() {
        TrainingSessionRequest r = new TrainingSessionRequest();
        r.setReportId(1L); r.setTrainingType(TrainingType.PARENT_TRAINING);
        r.setSlNo(1); r.setDate(LocalDate.now()); r.setPlace("School Hall");
        r.setSchoolName("ABC School"); r.setParticipantsMale(10);
        r.setParticipantsFemale(15); r.setParticipantsTotal(25);
        return r;
    }

    private TrainingSessionRequest requestWithNullParticipants() {
        TrainingSessionRequest r = new TrainingSessionRequest();
        r.setReportId(1L); r.setTrainingType(TrainingType.TEACHER_TRAINING);
        r.setParticipantsMale(null); r.setParticipantsFemale(null); r.setParticipantsTotal(null);
        return r;
    }

    @Test
    void getSessionsByReport_returnsList() {
        when(sessionRepository.findByReportId(1L)).thenReturn(List.of(session(false)));
        assertThat(trainingSessionService.getSessionsByReport(1L)).hasSize(1);
    }

    @Test
    void getSessionsByReportAndType_returnsList() {
        when(sessionRepository.findByReportIdAndTrainingType(1L, TrainingType.TEACHER_TRAINING))
                .thenReturn(List.of(session(false)));
        assertThat(trainingSessionService.getSessionsByReportAndType(1L, TrainingType.TEACHER_TRAINING)).hasSize(1);
    }

    @Test
    void getSessionById_found_returnsSession() {
        TrainingSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        assertThat(trainingSessionService.getSessionById(1L)).isEqualTo(s);
    }

    @Test
    void getSessionById_notFound_throwsResourceNotFoundException() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trainingSessionService.getSessionById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void addSession_unlockedReport_savesSession() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(false)));
        TrainingSession saved = session(false);
        when(sessionRepository.save(any(TrainingSession.class))).thenReturn(saved);

        assertThat(trainingSessionService.addSession(request(), "user1")).isNotNull();
    }

    @Test
    void addSession_withNullParticipants_defaultsToZero() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(false)));
        when(sessionRepository.save(any(TrainingSession.class))).thenReturn(session(false));
        trainingSessionService.addSession(requestWithNullParticipants(), "user1");
        verify(sessionRepository).save(argThat(s ->
                s.getParticipantsMale() == 0 && s.getParticipantsFemale() == 0 && s.getParticipantsTotal() == 0));
    }

    @Test
    void addSession_lockedReport_throwsIllegalStateException() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(true)));
        assertThatThrownBy(() -> trainingSessionService.addSession(request(), "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void addSession_reportNotFound_throwsResourceNotFoundException() {
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> trainingSessionService.addSession(request(), "user1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateSession_unlockedReport_updatesSession() {
        TrainingSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        when(sessionRepository.save(s)).thenReturn(s);
        trainingSessionService.updateSession(1L, request(), "user1");
        assertThat(s.getPlace()).isEqualTo("School Hall");
        assertThat(s.getParticipantsMale()).isEqualTo(10);
    }

    @Test
    void updateSession_withNullParticipants_defaultsToZero() {
        TrainingSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        when(sessionRepository.save(s)).thenReturn(s);
        trainingSessionService.updateSession(1L, requestWithNullParticipants(), "user1");
        assertThat(s.getParticipantsMale()).isEqualTo(0);
        assertThat(s.getParticipantsFemale()).isEqualTo(0);
    }

    @Test
    void updateSession_lockedReport_throwsIllegalStateException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session(true)));
        assertThatThrownBy(() -> trainingSessionService.updateSession(1L, request(), "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void deleteSession_unlockedReport_deletesSession() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session(false)));
        trainingSessionService.deleteSession(1L, "user1");
        verify(sessionRepository).deleteById(1L);
    }

    @Test
    void deleteSession_lockedReport_throwsIllegalStateException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session(true)));
        assertThatThrownBy(() -> trainingSessionService.deleteSession(1L, "user1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }
}
