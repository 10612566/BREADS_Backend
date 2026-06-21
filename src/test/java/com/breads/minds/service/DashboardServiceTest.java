package com.breads.minds.service;

import com.breads.minds.dto.response.DashboardResponse;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.District;
import com.breads.minds.entity.enums.ProposalStatus;
import com.breads.minds.entity.enums.UserStatus;
import com.breads.minds.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock private BeneficiaryReportRepository reportRepository;
    @Mock private SchoolRepository schoolRepository;
    @Mock private DistrictRepository districtRepository;
    @Mock private UserRepository userRepository;
    @Mock private ServiceRequestRepository serviceRequestRepository;
    @Mock private MindsActivityRecordRepository activityRepository;
    @InjectMocks private DashboardService dashboardService;

    private BeneficiaryReport reportWithData(String month, String districtName) {
        District d = District.builder().id(1L).name(districtName).build();
        return BeneficiaryReport.builder()
                .district(d).month(month).year(2025)
                .childrenReached(100).parentsReached(50)
                .professionalsReached(5).teachersReached(20).volunteersReached(10)
                .isLocked(false).build();
    }

    private void mockCommonCalls(List<BeneficiaryReport> reports) {
        when(reportRepository.findByYear(anyInt())).thenReturn(reports);
        when(reportRepository.findByMonthOrderByDistrictIdAsc(anyString())).thenReturn(reports);
        when(schoolRepository.count()).thenReturn(5L);
        when(schoolRepository.findByIsActive(true)).thenReturn(List.of());
        when(districtRepository.count()).thenReturn(3L);
        when(userRepository.count()).thenReturn(10L);
        when(serviceRequestRepository.countByStatus(ProposalStatus.PENDING)).thenReturn(2L);
        when(userRepository.findByStatus(UserStatus.PENDING)).thenReturn(List.of());
        when(activityRepository.count()).thenReturn(50L);
    }

    @Test
    void getDashboard_withYearProvided_returnsCorrectResponse() {
        List<BeneficiaryReport> reports = List.of(reportWithData("2025-01", "District A"));
        mockCommonCalls(reports);

        DashboardResponse response = dashboardService.getDashboard(2025);

        assertThat(response.getTotalChildrenReached()).isEqualTo(100);
        assertThat(response.getTotalParentsReached()).isEqualTo(50);
        assertThat(response.getGrandTotalBeneficiaries()).isEqualTo(185);
        assertThat(response.getTargetChildren()).isEqualTo(18000);
        assertThat(response.getTargetParents()).isEqualTo(18000);
        assertThat(response.getTargetProfessionals()).isEqualTo(60);
        assertThat(response.getTargetTeachers()).isEqualTo(300);
        assertThat(response.getTargetVolunteers()).isEqualTo(60);
        assertThat(response.getTotalDistricts()).isEqualTo(3);
        assertThat(response.getMonthlyBeneficiaryTrend()).hasSize(12);
        assertThat(response.getPendingServiceRequests()).isEqualTo(2);
        assertThat(response.getMindsActivityRecords()).isEqualTo(50);
    }

    @Test
    void getDashboard_withNullYear_usesCurrentYear() {
        List<BeneficiaryReport> reports = List.of(reportWithData("2025-06", "District B"));
        mockCommonCalls(reports);

        DashboardResponse response = dashboardService.getDashboard(null);

        assertThat(response).isNotNull();
        assertThat(response.getMonthlyBeneficiaryTrend()).hasSize(12);
    }

    @Test
    void getDashboard_withNullFieldsInReport_handlesNullsAsZero() {
        District d = District.builder().id(1L).name("D1").build();
        BeneficiaryReport report = BeneficiaryReport.builder()
                .district(d).month("2025-03").year(2025)
                .childrenReached(null).parentsReached(null)
                .professionalsReached(null).teachersReached(null)
                .volunteersReached(null).build();

        when(reportRepository.findByYear(2025)).thenReturn(List.of(report));
        when(reportRepository.findByMonthOrderByDistrictIdAsc(anyString())).thenReturn(Collections.emptyList());
        when(schoolRepository.count()).thenReturn(0L);
        when(schoolRepository.findByIsActive(true)).thenReturn(Collections.emptyList());
        when(districtRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(serviceRequestRepository.countByStatus(ProposalStatus.PENDING)).thenReturn(0L);
        when(userRepository.findByStatus(UserStatus.PENDING)).thenReturn(Collections.emptyList());
        when(activityRepository.count()).thenReturn(0L);

        DashboardResponse response = dashboardService.getDashboard(2025);

        assertThat(response.getGrandTotalBeneficiaries()).isEqualTo(0);
        assertThat(response.getTotalChildrenReached()).isEqualTo(0);
    }

    @Test
    void getDashboard_emptyReports_returnsZeroTotals() {
        when(reportRepository.findByYear(anyInt())).thenReturn(Collections.emptyList());
        when(reportRepository.findByMonthOrderByDistrictIdAsc(anyString())).thenReturn(Collections.emptyList());
        when(schoolRepository.count()).thenReturn(0L);
        when(schoolRepository.findByIsActive(true)).thenReturn(Collections.emptyList());
        when(districtRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(serviceRequestRepository.countByStatus(ProposalStatus.PENDING)).thenReturn(0L);
        when(userRepository.findByStatus(UserStatus.PENDING)).thenReturn(Collections.emptyList());
        when(activityRepository.count()).thenReturn(0L);

        DashboardResponse response = dashboardService.getDashboard(2025);

        assertThat(response.getTotalChildrenReached()).isEqualTo(0);
        assertThat(response.getBeneficiariesByDistrict()).isEmpty();
        assertThat(response.getReportsSubmitted()).isEqualTo(0);
    }

    @Test
    void getDashboard_multipleReportsMultipleDistricts_aggregatesByDistrict() {
        List<BeneficiaryReport> reports = List.of(
                reportWithData("2025-01", "D1"),
                reportWithData("2025-02", "D1"),
                reportWithData("2025-03", "D2")
        );
        mockCommonCalls(reports);

        DashboardResponse response = dashboardService.getDashboard(2025);
        assertThat(response.getBeneficiariesByDistrict()).containsKey("D1");
        assertThat(response.getBeneficiariesByDistrict()).containsKey("D2");
        assertThat(response.getBeneficiariesByDistrict().get("D1")).isEqualTo(370L);
        assertThat(response.getBeneficiariesByDistrict().get("D2")).isEqualTo(185L);
    }

    @Test
    void getDashboard_monthlyTrendWithNullFields_treatsNullAsZero() {
        District d = District.builder().id(1L).name("D1").build();
        BeneficiaryReport reportWithNulls = BeneficiaryReport.builder()
                .district(d).month("2025-06").year(2025)
                .childrenReached(null).parentsReached(null)
                .professionalsReached(null).teachersReached(null)
                .volunteersReached(null).build();

        when(reportRepository.findByYear(2025)).thenReturn(Collections.emptyList());
        when(reportRepository.findByMonthOrderByDistrictIdAsc(anyString())).thenReturn(List.of(reportWithNulls));
        when(schoolRepository.count()).thenReturn(0L);
        when(schoolRepository.findByIsActive(true)).thenReturn(Collections.emptyList());
        when(districtRepository.count()).thenReturn(0L);
        when(userRepository.count()).thenReturn(0L);
        when(serviceRequestRepository.countByStatus(ProposalStatus.PENDING)).thenReturn(0L);
        when(userRepository.findByStatus(UserStatus.PENDING)).thenReturn(Collections.emptyList());
        when(activityRepository.count()).thenReturn(0L);

        DashboardResponse response = dashboardService.getDashboard(2025);
        assertThat(response.getMonthlyBeneficiaryTrend()).hasSize(12);
        response.getMonthlyBeneficiaryTrend().values()
                .forEach(v -> assertThat(v).isEqualTo(0L));
    }
}
