package com.breads.minds.service;

import com.breads.minds.dto.request.AdditionalSessionRequest;
import com.breads.minds.entity.AdditionalSession;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.enums.AdditionalSessionType;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.AdditionalSessionRepository;
import com.breads.minds.repository.BeneficiaryReportRepository;
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
class AdditionalSessionServiceTest {

    @Mock private AdditionalSessionRepository sessionRepository;
    @Mock private BeneficiaryReportRepository reportRepository;
    @InjectMocks private AdditionalSessionService additionalSessionService;

    private BeneficiaryReport report(Boolean locked) {
        return BeneficiaryReport.builder().id(1L).isLocked(locked).build();
    }

    private AdditionalSession session(Boolean reportLocked) {
        return AdditionalSession.builder().id(1L).report(report(reportLocked))
                .sessionType(AdditionalSessionType.EXTRA_CLASS).build();
    }

    private AdditionalSessionRequest request() {
        AdditionalSessionRequest r = new AdditionalSessionRequest();
        r.setReportId(1L); r.setSessionType(AdditionalSessionType.EXTRA_CLASS);
        r.setSlNo(1); r.setDate(LocalDate.now()); r.setPlace("Class Room");
        r.setSchoolName("XYZ School"); r.setParticipantsMale(10);
        r.setParticipantsFemale(12); r.setParticipantsTotal(22);
        r.setRemarks("Good session");
        return r;
    }

    private AdditionalSessionRequest requestWithNullParticipants() {
        AdditionalSessionRequest r = new AdditionalSessionRequest();
        r.setReportId(1L); r.setSessionType(AdditionalSessionType.ADDITIONAL_COUNSELLING);
        r.setParticipantsMale(null); r.setParticipantsFemale(null); r.setParticipantsTotal(null);
        return r;
    }

    @Test
    void getByReport_returnsList() {
        when(sessionRepository.findByReportId(1L)).thenReturn(List.of(session(false)));
        assertThat(additionalSessionService.getByReport(1L)).hasSize(1);
    }

    @Test
    void getByReportAndType_returnsList() {
        when(sessionRepository.findByReportIdAndSessionType(1L, AdditionalSessionType.EXTRA_CLASS))
                .thenReturn(List.of(session(false)));
        assertThat(additionalSessionService.getByReportAndType(1L, AdditionalSessionType.EXTRA_CLASS)).hasSize(1);
    }

    @Test
    void getById_found_returnsSession() {
        AdditionalSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        assertThat(additionalSessionService.getById(1L)).isEqualTo(s);
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(sessionRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> additionalSessionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void add_unlockedReport_savesSession() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(false)));
        when(sessionRepository.save(any(AdditionalSession.class))).thenReturn(session(false));
        assertThat(additionalSessionService.add(request())).isNotNull();
    }

    @Test
    void add_withNullParticipants_defaultsToZero() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(false)));
        when(sessionRepository.save(any(AdditionalSession.class))).thenReturn(session(false));
        additionalSessionService.add(requestWithNullParticipants());
        verify(sessionRepository).save(argThat(s ->
                s.getParticipantsMale() == 0 && s.getParticipantsFemale() == 0 && s.getParticipantsTotal() == 0));
    }

    @Test
    void add_reportNotFound_throwsResourceNotFoundException() {
        when(reportRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> additionalSessionService.add(request()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void add_lockedReport_throwsIllegalStateException() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(true)));
        assertThatThrownBy(() -> additionalSessionService.add(request()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void update_unlockedReport_updatesSession() {
        AdditionalSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        when(sessionRepository.save(s)).thenReturn(s);
        additionalSessionService.update(1L, request());
        assertThat(s.getPlace()).isEqualTo("Class Room");
        assertThat(s.getRemarks()).isEqualTo("Good session");
    }

    @Test
    void update_withNullParticipants_defaultsToZero() {
        AdditionalSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        when(sessionRepository.save(s)).thenReturn(s);
        additionalSessionService.update(1L, requestWithNullParticipants());
        assertThat(s.getParticipantsMale()).isEqualTo(0);
        assertThat(s.getParticipantsFemale()).isEqualTo(0);
    }

    @Test
    void update_lockedReport_throwsIllegalStateException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session(true)));
        assertThatThrownBy(() -> additionalSessionService.update(1L, request()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void delete_unlockedReport_deletesSession() {
        AdditionalSession s = session(false);
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(s));
        additionalSessionService.delete(1L);
        verify(sessionRepository).delete(s);
    }

    @Test
    void delete_lockedReport_throwsIllegalStateException() {
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session(true)));
        assertThatThrownBy(() -> additionalSessionService.delete(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }
}
