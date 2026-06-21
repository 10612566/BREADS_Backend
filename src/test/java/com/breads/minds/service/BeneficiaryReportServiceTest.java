package com.breads.minds.service;

import com.breads.minds.dto.request.BeneficiaryReportRequest;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.District;
import com.breads.minds.entity.User;
import com.breads.minds.entity.enums.UserRole;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.exception.ResourceNotFoundException;
import com.breads.minds.repository.BeneficiaryReportRepository;
import com.breads.minds.repository.DistrictRepository;
import com.breads.minds.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiaryReportServiceTest {

    @Mock private BeneficiaryReportRepository reportRepository;
    @Mock private DistrictRepository districtRepository;
    @Mock private UserRepository userRepository;
    @Mock private SystemLogService systemLogService;
    @InjectMocks private BeneficiaryReportService reportService;

    private District district() {
        return District.builder().id(1L).name("D1").build();
    }

    private User user() {
        return User.builder().id(1L).username("u1").role(UserRole.DISTRICT_COORDINATOR)
                .status(UserStatus.ACTIVE).build();
    }

    private BeneficiaryReport report(Boolean locked) {
        return BeneficiaryReport.builder().id(1L).district(district())
                .month("2025-01").year(2025).isLocked(locked)
                .childrenReached(10).parentsReached(5).build();
    }

    private BeneficiaryReportRequest request() {
        BeneficiaryReportRequest r = new BeneficiaryReportRequest();
        r.setDistrictId(1L); r.setMonth("2025-06"); r.setYear(2025);
        r.setChildrenReached(100); r.setParentsReached(50);
        r.setProfessionalsReached(5); r.setTeachersReached(20);
        r.setVolunteersReached(10); r.setModules(3);
        r.setCommunityAwareness(2); r.setArtTherapy(1); r.setCounselling(4);
        r.setNarrativeImpact("Great impact");
        return r;
    }

    @Test
    void getAllReports_returnsList() {
        when(reportRepository.findAll()).thenReturn(List.of(report(false)));
        assertThat(reportService.getAllReports()).hasSize(1);
    }

    @Test
    void getReportsByDistrict_returnsList() {
        when(reportRepository.findByDistrictId(1L)).thenReturn(List.of(report(false)));
        assertThat(reportService.getReportsByDistrict(1L)).hasSize(1);
    }

    @Test
    void getReportsByYear_returnsList() {
        when(reportRepository.findByYear(2025)).thenReturn(List.of(report(false)));
        assertThat(reportService.getReportsByYear(2025)).hasSize(1);
    }

    @Test
    void getReportsByDistrictAndYear_returnsList() {
        when(reportRepository.findByDistrictIdAndYear(1L, 2025)).thenReturn(List.of(report(false)));
        assertThat(reportService.getReportsByDistrictAndYear(1L, 2025)).hasSize(1);
    }

    @Test
    void getReportsByMonthRange_returnsList() {
        when(reportRepository.findByMonthBetween("2025-01", "2025-06")).thenReturn(List.of(report(false)));
        assertThat(reportService.getReportsByMonthRange("2025-01", "2025-06")).hasSize(1);
    }

    @Test
    void getReportsByDistrictAndMonthRange_returnsList() {
        when(reportRepository.findByDistrictIdAndMonthBetween(1L, "2025-01", "2025-06"))
                .thenReturn(List.of(report(false)));
        assertThat(reportService.getReportsByDistrictAndMonthRange(1L, "2025-01", "2025-06")).hasSize(1);
    }

    @Test
    void getReportById_found_returnsReport() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(false)));
        assertThat(reportService.getReportById(1L)).isNotNull();
    }

    @Test
    void getReportById_notFound_throwsResourceNotFoundException() {
        when(reportRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reportService.getReportById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void submitReport_success() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user()));
        when(reportRepository.existsByDistrictIdAndMonth(1L, "2025-06")).thenReturn(false);
        BeneficiaryReport saved = report(false);
        when(reportRepository.save(any(BeneficiaryReport.class))).thenReturn(saved);

        BeneficiaryReport result = reportService.submitReport(request(), "u1");
        assertThat(result).isNotNull();
    }

    @Test
    void submitReport_districtNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reportService.submitReport(request(), "u1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void submitReport_userNotFound_throwsResourceNotFoundException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reportService.submitReport(request(), "u1"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void submitReport_duplicateReport_throwsIllegalStateException() {
        when(districtRepository.findById(1L)).thenReturn(Optional.of(district()));
        when(userRepository.findByUsername("u1")).thenReturn(Optional.of(user()));
        when(reportRepository.existsByDistrictIdAndMonth(1L, "2025-06")).thenReturn(true);
        assertThatThrownBy(() -> reportService.submitReport(request(), "u1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already submitted");
    }

    @Test
    void updateReport_unlockedReport_updatesAndSaves() {
        BeneficiaryReport rep = report(false);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(rep));
        when(reportRepository.save(rep)).thenReturn(rep);

        reportService.updateReport(1L, request(), "u1");
        assertThat(rep.getChildrenReached()).isEqualTo(100);
        assertThat(rep.getParentsReached()).isEqualTo(50);
    }

    @Test
    void updateReport_lockedReport_throwsIllegalStateException() {
        when(reportRepository.findById(1L)).thenReturn(Optional.of(report(true)));
        assertThatThrownBy(() -> reportService.updateReport(1L, request(), "u1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("locked");
    }

    @Test
    void lockReport_setsLockedTrueAndSaves() {
        BeneficiaryReport rep = report(false);
        when(reportRepository.findById(1L)).thenReturn(Optional.of(rep));
        when(reportRepository.save(rep)).thenReturn(rep);

        reportService.lockReport(1L, "admin");
        assertThat(rep.getIsLocked()).isTrue();
    }
}
