package com.breads.minds.service;

import com.breads.minds.dto.response.DashboardResponse;
import com.breads.minds.entity.BeneficiaryReport;
import com.breads.minds.entity.enums.ProposalStatus;
import com.breads.minds.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final BeneficiaryReportRepository reportRepository;
    private final SchoolRepository schoolRepository;
    private final DistrictRepository districtRepository;
    private final UserRepository userRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final MindsActivityRecordRepository activityRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Integer year) {
        int currentYear = year != null ? year : LocalDate.now().getYear();
        String currentMonth = String.format("%d-%02d", LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        List<BeneficiaryReport> yearlyReports = reportRepository.findByYear(currentYear);

        long children      = yearlyReports.stream().mapToLong(r -> r.getChildrenReached()      != null ? r.getChildrenReached()      : 0).sum();
        long parents       = yearlyReports.stream().mapToLong(r -> r.getParentsReached()       != null ? r.getParentsReached()       : 0).sum();
        long professionals = yearlyReports.stream().mapToLong(r -> r.getProfessionalsReached() != null ? r.getProfessionalsReached() : 0).sum();
        long teachers      = yearlyReports.stream().mapToLong(r -> r.getTeachersReached()      != null ? r.getTeachersReached()      : 0).sum();
        long volunteers    = yearlyReports.stream().mapToLong(r -> r.getVolunteersReached()    != null ? r.getVolunteersReached()    : 0).sum();

        // Per-district beneficiary totals
        Map<String, Long> byDistrict = yearlyReports.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getDistrict().getName(),
                        Collectors.summingLong(r ->
                                (r.getChildrenReached()      != null ? r.getChildrenReached()      : 0) +
                                (r.getParentsReached()       != null ? r.getParentsReached()       : 0) +
                                (r.getProfessionalsReached() != null ? r.getProfessionalsReached() : 0) +
                                (r.getTeachersReached()      != null ? r.getTeachersReached()      : 0) +
                                (r.getVolunteersReached()    != null ? r.getVolunteersReached()    : 0)
                        )
                ));

        // Monthly trend (last 12 months)
        Map<String, Long> monthlyTrend = buildMonthlyTrend(currentYear);

        long totalDistricts = districtRepository.count();

        return DashboardResponse.builder()
                .totalChildrenReached(children)
                .totalParentsReached(parents)
                .totalProfessionalsReached(professionals)
                .totalTeachersReached(teachers)
                .totalVolunteersReached(volunteers)
                .grandTotalBeneficiaries(children + parents + professionals + teachers + volunteers)
                .targetChildren(18000)
                .targetParents(18000)
                .targetProfessionals(60)
                .targetTeachers(300)
                .targetVolunteers(60)
                .totalSchools(schoolRepository.count())
                .activeSchools(schoolRepository.findByIsActive(true).size())
                .totalDistricts(totalDistricts)
                .totalUsers(userRepository.count())
                .pendingServiceRequests(serviceRequestRepository.countByStatus(ProposalStatus.PENDING))
                .pendingUserApprovals(userRepository.findByStatus(
                        com.breads.minds.entity.enums.UserStatus.PENDING).size())
                .mindsActivityRecords(activityRepository.count())
                .beneficiariesByDistrict(byDistrict)
                .monthlyBeneficiaryTrend(monthlyTrend)
                .reportsSubmitted(reportRepository.findByMonthOrderByDistrictIdAsc(currentMonth).size())
                .reportsExpected(totalDistricts)
                .build();
    }

    private Map<String, Long> buildMonthlyTrend(int year) {
        Map<String, Long> trend = new LinkedHashMap<>();
        for (int m = 1; m <= 12; m++) {
            String month = String.format("%d-%02d", year, m);
            long total = reportRepository.findByMonthOrderByDistrictIdAsc(month).stream()
                    .mapToLong(r ->
                            (r.getChildrenReached()      != null ? r.getChildrenReached()      : 0) +
                            (r.getParentsReached()       != null ? r.getParentsReached()       : 0) +
                            (r.getProfessionalsReached() != null ? r.getProfessionalsReached() : 0) +
                            (r.getTeachersReached()      != null ? r.getTeachersReached()      : 0) +
                            (r.getVolunteersReached()    != null ? r.getVolunteersReached()    : 0))
                    .sum();
            trend.put(month, total);
        }
        return trend;
    }
}
